package ua.flowerista.shop.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ua.flowerista.shop.dto.user.UserAuthenticationResponseDto;
import ua.flowerista.shop.dto.user.UserLoginBodyDto;
import ua.flowerista.shop.models.RefreshToken;
import ua.flowerista.shop.models.Token;
import ua.flowerista.shop.models.TokenType;
import ua.flowerista.shop.models.User;
import ua.flowerista.shop.repo.TokenRepository;
import ua.flowerista.shop.repo.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final AuthenticationManager authenticationManager;


  public UserAuthenticationResponseDto authenticate(UserLoginBodyDto loginBody) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginBody.getEmail(),
            loginBody.getPassword()
        )
    );
    var user = repository.findByEmail(loginBody.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = refreshTokenService.createRefreshToken(user.getEmail()).getToken();
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return UserAuthenticationResponseDto.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public UserAuthenticationResponseDto refreshToken(HttpServletRequest request)  {
    String refreshToken = request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION).substring(7);
    return refreshTokenService.findByToken(refreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUserInfo)
            .map(user -> {
              String accessToken = jwtService.generateToken(user);
              revokeAllUserTokens(user);
              saveUserToken(user, accessToken);
              return UserAuthenticationResponseDto.builder()
                      .accessToken(accessToken)
                      .refreshToken(refreshToken)
                      .build();
            }).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
  }
}
