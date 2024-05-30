package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.configs.UserAuthenticationProvider;
import ua.flowerista.shop.dto.user.CredentialsDto;
import ua.flowerista.shop.dto.user.UserAuthenticationResponseDto;
import ua.flowerista.shop.dto.user.UserPasswordResetDto;
import ua.flowerista.shop.dto.user.UserRegistrationBodyDto;
import ua.flowerista.shop.models.User;
import ua.flowerista.shop.registration.OnRegistrationCompleteEvent;
import ua.flowerista.shop.services.RefreshTokenService;
import ua.flowerista.shop.services.UserService;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@Tag(name = "AUTH controller", description = "Operations with sign up and sign in")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @PostMapping(value = "/register", consumes = "application/json")
    @Operation(summary = "Register new user", description = "Returns bad request if something went wrong, and accepted if everything fine")
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "If email or phone number already exist"),
            @ApiResponse(responseCode = "202", description = "Data was accepted")})
    public ResponseEntity<?> registerNewUser(@RequestBody @Valid UserRegistrationBodyDto regDto,
                                             final HttpServletRequest request) {
        if (userService.existsByEmail(regDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        if (userService.existsByPhoneNumber(regDto.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Phone number already exists");
        }
        final User registered = userService.registerNewUserAccount(regDto);
        eventPublisher
                .publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/checkEmail/{email}")
    @Operation(summary = "Check if email exists", description = "Returns true - if exists, false - if not")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable(value = "email") String email) {
        boolean exists = userService.existsByEmail(email);
        if (exists == true) {
            return ResponseEntity.ok(exists);
        }
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/checkPhone/{phoneNumber}")
    @Operation(summary = "Check if phone number exists", description = "Returns true - if exists, false - if not")
    public ResponseEntity<Boolean> existsByPhoneNumber(@PathVariable(value = "phoneNumber") int phoneNumber) {
        boolean exists = userService.existsByPhoneNumber(phoneNumber);
        if (exists == true) {
            return ResponseEntity.ok(exists);
        }
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "User login endpoint", description = "Returns refresh and access tokens")
    @ApiResponses(value = {@ApiResponse(responseCode = "403", description = "If email or password didnt match"),
            @ApiResponse(responseCode = "200", description = "If data was accepted")})
    public ResponseEntity<?> authenticate(@RequestBody CredentialsDto credentialsDto, HttpServletResponse response) {
        UserAuthenticationResponseDto responseDto = UserAuthenticationResponseDto.builder()
                .user(userService.login(credentialsDto))
                .accessToken(userAuthenticationProvider.createAccessToken(credentialsDto.getEmail()))
                .build();
        refreshTokenService.setRefreshToken(credentialsDto.getEmail(), response);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Api to refresh token", description = "Returns refreshed access token if refresh token is valid" +
            " and present in cookies")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String login = refreshTokenService.refreshRefreshTokenAndGetLogin(request, response);
        UserAuthenticationResponseDto responseDto = UserAuthenticationResponseDto.builder()
                .user(userService.findByLogin(login))
                .accessToken(userAuthenticationProvider.createAccessToken(login))
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/registrationConfirm/{token}")
    @Operation(summary = "Validating token", description = "If token is expired, deleting token and user")
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "If token is expired or invalid"),
            @ApiResponse(responseCode = "202", description = "Data was accepted")})
    public ResponseEntity<?> registrationConfirm(@PathVariable(value = "token") String token) {
        String tokenValidated = userService.validateVerificationToken(token);
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
        User user = userService.findUserByEmail(userEmail);
        if (user.getId() == 0) {
            return ResponseEntity.notFound().build();
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        mailSender.send(constructResetTokenEmail("flowerista-frontend.vercel.app", request.getLocale(), token, user));
        return ResponseEntity.accepted().body("Token was sent");
    }

    @PostMapping("/changePassword")
    @Operation(summary = "Restoring password with token", description = "Changing user password with token")
    public ResponseEntity<?> changePassword(@RequestBody @Valid UserPasswordResetDto dto) {
        String response = userService.resetPassword(dto);
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
