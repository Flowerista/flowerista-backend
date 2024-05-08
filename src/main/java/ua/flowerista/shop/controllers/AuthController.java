package ua.flowerista.shop.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ua.flowerista.shop.dto.user.UserAuthenticationResponseDto;
import ua.flowerista.shop.dto.user.UserLoginBodyDto;
import ua.flowerista.shop.dto.user.UserPasswordResetDto;
import ua.flowerista.shop.dto.user.UserRegistrationBodyDto;
import ua.flowerista.shop.mappers.UserMapper;
import ua.flowerista.shop.models.User;
import ua.flowerista.shop.registration.OnRegistrationCompleteEvent;
import ua.flowerista.shop.services.AuthenticationService;
import ua.flowerista.shop.services.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@Tag(name = "AUTH controller", description = "Operations with sign up and sign in")
public class AuthController {

	@Autowired
	private UserService service;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private AuthenticationService authService;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private Environment env;

	@PostMapping(value = "/register", consumes = "application/json")
	@Operation(summary = "Register new user", description = "Returns bad request if something went wrong, and accepted if everything fine")
	@ApiResponses(value = { @ApiResponse(responseCode = "400", description = "If email or phone number already exist"),
			@ApiResponse(responseCode = "202", description = "Data was accepted") })
	public ResponseEntity<?> registerNewUser(@RequestBody @Valid UserRegistrationBodyDto regDto,
			final HttpServletRequest request) {
		if (service.existsByEmail(regDto.getEmail())) {
			return ResponseEntity.badRequest().body("Email already exists");
		}
		if (service.existsByPhoneNumber(regDto.getPhoneNumber())) {
			return ResponseEntity.badRequest().body("Phone number already exists");
		}
		final User registered = service.registerNewUserAccount(regDto);
		eventPublisher
				.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
		return ResponseEntity.accepted().body(UserAuthenticationResponseDto.builder().user(userMapper.toDto(registered)).build());
	}

	@GetMapping("/checkEmail/{email}")
	@Operation(summary = "Check if email exists", description = "Returns true - if exists, false - if not")
	public ResponseEntity<Boolean> existsByEmail(@PathVariable(value = "email") String email) {
		boolean exists = service.existsByEmail(email);
		if (exists == true) {
			return ResponseEntity.ok(exists);
		}
		return ResponseEntity.ok(exists);
	}

	@GetMapping("/checkPhone/{phoneNumber}")
	@Operation(summary = "Check if phone number exists", description = "Returns true - if exists, false - if not")
	public ResponseEntity<Boolean> existsByPhoneNumber(@PathVariable(value = "phoneNumber") int phoneNumber) {
		boolean exists = service.existsByPhoneNumber(phoneNumber);
		if (exists == true) {
			return ResponseEntity.ok(exists);
		}
		return ResponseEntity.ok(exists);
	}

	@PostMapping("/authenticate")
	@Operation(summary = "User login endpoint", description = "Returns refresh and access tokens")
	@ApiResponses(value = { @ApiResponse(responseCode = "403", description = "If email or password didnt match"),
			@ApiResponse(responseCode = "200", description = "If data was accepted") })
	public ResponseEntity<?> authenticate(@RequestBody UserLoginBodyDto request) {
		UserAuthenticationResponseDto response = authService.authenticate(request);
		if (response.getAccessToken().equals(null) || response.getRefreshToken().equals(null)) {
			return ResponseEntity.badRequest().body("Email or password dont match");
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh-token")
	@Operation(summary = "Api to refresh token", description = "Returns refreshed tokens")
	public ResponseEntity<?> refreshToken(HttpServletRequest request) {
		try {
			UserAuthenticationResponseDto responseDto = authService.refreshToken(request);
			return ResponseEntity.ok(responseDto);
		} catch (Exception e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}

	}

	@PostMapping("/registrationConfirm/{token}")
	@Operation(summary = "Validating token", description = "If token is expired, deleting token and user")
	@ApiResponses(value = { @ApiResponse(responseCode = "400", description = "If token is expired or invalid"),
			@ApiResponse(responseCode = "202", description = "Data was accepted") })
	public ResponseEntity<?> registrationConfirm(@PathVariable(value = "token") String token) {
		String tokenValidated = service.validateVerificationToken(token);
		if (tokenValidated.equals("invalidToken")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.location(URI.create("http://flowerista-frontend.vercel.app/")).build();
		}
		if (tokenValidated.equals("expired")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.location(URI.create("http://flowerista-frontend.vercel.app/")).build();
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.header(HttpHeaders.LOCATION, "http://flowerista-frontend.vercel.app/login").build();
	}

	@PostMapping("/resetPassword")
	@Operation(summary = "Restoring access api", description = "Looks for user with email, and sends letter on email to restore access")
	public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestParam("email") String userEmail) {
		User user = service.findUserByEmail(userEmail);
		if (user.getId() == 0) {
			return ResponseEntity.notFound().build();
		}
		String token = UUID.randomUUID().toString();
		service.createPasswordResetTokenForUser(user, token);
		mailSender.send(constructResetTokenEmail("flowerista-frontend.vercel.app", request.getLocale(), token, user));
		return ResponseEntity.accepted().body("Token was sent");
	}

	@PostMapping("/changePassword")
	@Operation(summary = "Restoring password with token", description = "Changing user password with token")
	public ResponseEntity<?> changePassword(@RequestBody @Valid UserPasswordResetDto dto) {
		String response = service.resetPassword(dto);
		if (response.equals("Password changed")) {
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.badRequest().body(response);
	}

	private String getAppUrl(HttpServletRequest request) {
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}

	private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale,
			final String token, final User user) {
		final String url = contextPath + "/changePassword?token=" + token;
		final String message = "To reset your password, follow the link (link is valid only 24 hours)";
		return constructEmail("Flowerista Reset Password", message + " \r\n" + url, user);
	}

	private SimpleMailMessage constructEmail(String subject, String body, User user) {
		final SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject(subject);
		email.setText(body);
		email.setTo(user.getEmail());
		email.setFrom(env.getProperty("support.email"));
		return email;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	private Map<String, String> handleValidationExceptions(
	  MethodArgumentNotValidException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getAllErrors().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();
	        errors.put(fieldName, errorMessage);
	    });
	    return errors;
	}

}
