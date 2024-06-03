package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.configs.UserAuthenticationProvider;
import ua.flowerista.shop.dto.user.CredentialsDto;
import ua.flowerista.shop.dto.user.SignUpDto;
import ua.flowerista.shop.dto.user.UserDto;
import ua.flowerista.shop.dto.user.ResetPasswordDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.mappers.UserMapper;
import ua.flowerista.shop.models.User;
import ua.flowerista.shop.registration.OnRegistrationCompleteEvent;
import ua.flowerista.shop.services.RefreshTokenService;
import ua.flowerista.shop.services.UserService;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "AUTH controller", description = "Operations with sign up and sign in")
public class AuthController {


    private final UserService userService;
    private final UserMapper userMapper;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher eventPublisher;


    @PostMapping(value = "/register", consumes = "application/json")
    @Operation(summary = "Register new user",
            description = "Returns bad request if something went wrong, and accepted if everything fine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "If email or phone number already exist"),
            @ApiResponse(responseCode = "202", description = "Data was accepted")})
    public ResponseEntity<?> register(@RequestBody @Valid SignUpDto regDto,
                                      final HttpServletRequest request) {
        final User registered = userService.registerNewUserAccount(regDto);
        eventPublisher
                .publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/checkEmail/{email}")
    @Operation(summary = "Check if email already used for registration",
            description = "Returns true - if exists, false - if not")
    public ResponseEntity<Boolean> checkEmail(@PathVariable(value = "email") String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @GetMapping("/checkPhone/{phoneNumber}")
    @Operation(summary = "Check if phone number already used for registration",
            description = "Returns true - if exists, false - if not")
    public ResponseEntity<Boolean> checkPhoneNumber(@PathVariable(value = "phoneNumber") Integer phoneNumber) {
        return ResponseEntity.ok(userService.existsByPhoneNumber(phoneNumber));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "User login endpoint",
            description = "Returns refresh and access tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "If email or password didnt match"),
            @ApiResponse(responseCode = "200", description = "If data was accepted")})
    public ResponseEntity<?> login(@RequestBody CredentialsDto credentialsDto,
                                   HttpServletResponse response) {
        UserDto userDto = userService.login(credentialsDto)
                .map(userMapper::toDto)
                .orElseThrow(() -> new AppException("Login failed", HttpStatus.FORBIDDEN));
        userDto.setAccessToken(userAuthenticationProvider.createAccessToken(userDto));
        refreshTokenService.setRefreshToken(userDto.getId(), response);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Api to refresh token",
            description = "Returns refreshed access token if refresh token is valid and present in cookies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "If token is expired or invalid"),
            @ApiResponse(responseCode = "200", description = "Refresh token was accepted")})
    public ResponseEntity<?> refreshToken(HttpServletRequest request,
                                          HttpServletResponse response) {
        Integer userId = refreshTokenService.refreshRefreshTokenAndGetUserId(request, response);
        UserDto userDto = userService.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.UNAUTHORIZED));
        userDto.setAccessToken(userAuthenticationProvider.createAccessToken(userDto));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/registrationConfirm/{token}")
    @Operation(summary = "Validating token",
            description = "If token is expired, deleting token and user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "If token is expired or invalid"),
            @ApiResponse(responseCode = "202", description = "Data was accepted")})
    public ResponseEntity<?> registrationConfirm(@PathVariable(value = "token") String token) {
        try {
            userService.processRegistrationToken(token);
        } catch (AppException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .location(URI.create("http://flowerista-frontend.vercel.app/"))
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header(HttpHeaders.LOCATION, "http://flowerista-frontend.vercel.app/login")
                .build();
    }

    @PostMapping("/resetPassword")
    @Operation(summary = "Restoring access api",
            description = "Sending email with restoring link to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "If user with email not found"),
            @ApiResponse(responseCode = "202", description = "Email with restoring link was sent")})
    public ResponseEntity<?> sendLinkForPasswordReset(@RequestParam("email") String email) {
        Optional<User> user = userService.findByLogin(email);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Unknown user");
        } else {
            userService.sendPasswordResetEmail(user.get());
            return ResponseEntity.accepted().body("Email with restoring link was sent");
        }
    }

    @PostMapping("/changePassword")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Restoring password with token", description = "Restore password with token from email link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "If token is expired or invalid"),
            @ApiResponse(responseCode = "200", description = "Password changed")})
    public void setPasswordWithRestoringToken(@RequestBody @Valid ResetPasswordDto dto) {
        userService.resetPassword(dto);
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
