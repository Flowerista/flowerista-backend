package ua.flowerista.shop.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import ua.flowerista.shop.dto.user.UserAuthenticationResponseDto;
import ua.flowerista.shop.dto.user.UserLoginBodyDto;
import ua.flowerista.shop.mappers.UserMapper;
import ua.flowerista.shop.models.RefreshToken;
import ua.flowerista.shop.models.Token;
import ua.flowerista.shop.models.TokenType;
import ua.flowerista.shop.models.User;
import ua.flowerista.shop.repo.TokenRepository;
import ua.flowerista.shop.repo.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
  Logger logger = Logger.getLogger(AuthenticationService.class.getName());

  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final AuthenticationManager authenticationManager;
  private final UserMapper userMapper;
  @Value("${jwt.cookie.expiration}")
  private long cookieExpiration;


  public UserAuthenticationResponseDto authenticate(UserLoginBodyDto loginBody, HttpServletResponse response) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginBody.getEmail(),
            loginBody.getPassword()
        )
    );
    if (authentication.isAuthenticated()) {
      var user = repository.findByEmail(loginBody.getEmail())
              .orElseThrow();
      return getUserAuthenticationResponseDtoWithRefreshTokenInCookie(response, user);
    } else {
      throw new RuntimeException("Authentication failed");
    }
  }

  private void saveUserToken(User user, String jwtToken) {
    try {
//      if (tokenRepository.findByToken(jwtToken).isPresent()) {
//        tokenRepository.deleteByToken(jwtToken);
//      }
      var token = Token.builder()
              .user(user)
              .token(jwtToken)
              .tokenType(TokenType.BEARER)
              .expired(false)
              .revoked(false)
              .build();
      if (tokenRepository.findByToken(jwtToken).isEmpty()) {
        tokenRepository.save(token);
      }
    } catch (Exception e) {
      logger.info("Error saving token: " + e.getMessage());
    }

  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
//    tokenRepository.updateRevokedAndExpiredByTokenIn(validUserTokens.stream().map(Token::getToken).toList());
    tokenRepository.saveAll(validUserTokens);
  }
  @Transactional
  public UserAuthenticationResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response)  {
    String refreshToken = getRefreshTokenFromCookie(request);
    return refreshTokenService.findByToken(refreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUserInfo)
            .map(user -> getUserAuthenticationResponseDtoWithRefreshTokenInCookie(response, user)
            ).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
  }

  private String getRefreshTokenFromCookie(HttpServletRequest request) {
    String refreshToken = null;
    if(request.getCookies() != null){
      for(Cookie cookie: request.getCookies()){
        if(cookie.getName().equals("refreshToken")){
          refreshToken = cookie.getValue();
        }
      }
    }
    if(refreshToken == null){
      throw new RuntimeException("Refresh token not found in cookies");
    }
    return refreshToken;
  }

  private void setRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/api/auth/refresh-token")
            .maxAge(cookieExpiration)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  private UserAuthenticationResponseDto getUserAuthenticationResponseDtoWithRefreshTokenInCookie(HttpServletResponse response, User user) {
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = refreshTokenService.createRefreshToken(user.getEmail()).getToken();
    setRefreshTokenToCookie(response, refreshToken);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return UserAuthenticationResponseDto.builder()
            .accessToken(jwtToken)
            .user(userMapper.toProfileDto(user))
            .build();
  }
}
