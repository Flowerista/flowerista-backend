package ua.flowerista.shop.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.exceptions.AppException;

import java.util.*;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RedisService redisService;

    @Value("${security.jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    @Value("${jwt.cookie.expiration}")
    private Long cookieExpiration;

    public void setRefreshToken(Integer userId, HttpServletResponse response) {
        String refreshToken = createRefreshToken(userId);
        setRefreshTokenCookie(refreshToken, response);
    }

    public Integer refreshRefreshTokenAndGetUserId(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        Map<String, String> tokenInfo = redisService.getHashMap(refreshToken);
        if (!tokenInfo.isEmpty()) {
            validateToken(tokenInfo);

            //revoking old token
            revokeRefreshToken(tokenInfo);

            String newRefreshToken = createRefreshToken(tokenInfo.get("userId"));

            setRefreshTokenCookie(newRefreshToken, response);

            return Integer.valueOf(tokenInfo.get("userId"));
        } else {
            deleteRefreshTokenFromCookie(response);
            throw new AppException("Refresh token not found in server", HttpStatus.UNAUTHORIZED);
        }
    }

    public void revokeUserRefreshTokens(String login) {
        Set<String> tokens = redisService.getSet(login);
        for (String token : tokens) {
            Map<String, String> tokenInfo = redisService.getHashMap(token);
            if (tokenInfo != null) {
                revokeRefreshToken(tokenInfo);
            }
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> tokenInfo = redisService.getHashMap(extractRefreshTokenFromCookie(request));
        if (tokenInfo != null) {
            deleteRefreshTokenForUser(tokenInfo.get("userId"), tokenInfo.get("token"));
        }
        deleteRefreshTokenFromCookie(response);
    }

    private static void deleteRefreshTokenFromCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/auth/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        if (refreshToken == null) {
            throw new AppException("Refresh token not found in cookie", HttpStatus.UNAUTHORIZED);
        }
        return refreshToken;
    }

    private void deleteAllRefreshTokensForUser(String login) {
        Set<String> tokens = redisService.getSet(login);
        for (String token : tokens) {
            redisService.deleteByKey(token);
        }
    }

    private void deleteAllRefreshTokensForUser(Integer userId) {
        Set<String> tokens = redisService.getSet(userId.toString());
        for (String token : tokens) {
            redisService.deleteByKey(token);
        }
    }
    private void deleteRefreshTokenForUser(String login, String refreshToken) {
        Set<String> tokens = redisService.getSet(login);
        if (tokens != null) {
            tokens.remove(refreshToken);
            redisService.saveSet(login, tokens);
            redisService.deleteByKey(refreshToken);
        }
    }

    private void revokeRefreshToken(Map<String, String> tokenInfo) {
        tokenInfo.put("revoked", "true");
        redisService.saveHashMap(tokenInfo.get("token"), tokenInfo, refreshTokenExpiration);
    }

    private void validateToken(Map<String, String> tokenInfo) {
        if (tokenInfo.get("revoked").equals("true")) {
            deleteAllRefreshTokensForUser(tokenInfo.get("userId"));
            throw new AppException("Using revoked token", HttpStatus.UNAUTHORIZED);
        }
    }

    private String createRefreshToken(String userId) {
        String refreshToken = UUID.randomUUID().toString();

        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("userId", userId);
        tokenInfo.put("revoked", "false");
        tokenInfo.put("token", refreshToken);

        redisService.saveHashMap(refreshToken, tokenInfo, refreshTokenExpiration);

        addTokenToUserTokensList(userId, refreshToken);

        return refreshToken;
    }

    private String createRefreshToken(Integer userId) {
        return createRefreshToken(userId.toString());
    }

    private void addTokenToUserTokensList(String login, String refreshToken) {
        Set<String> tokens = redisService.getSet(login);

        if (tokens == null) {
            redisService.saveSet(login, new HashSet<>(List.of(refreshToken)));
        } else {
            deleteExpiredTokens(tokens);
            tokens.add(refreshToken);
            redisService.saveSet(login, tokens);
        }
    }

    private void deleteExpiredTokens(Set<String> tokens) {
        for (String token : tokens) {
            Map<String, String> tokenInfo = redisService.getHashMap(token);
            if (tokenInfo == null) {
                tokens.remove(token);
            }
        }
    }

    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/auth/")
                .maxAge(cookieExpiration)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


}
