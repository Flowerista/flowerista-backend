package ua.flowerista.shop.services;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.flowerista.shop.exceptions.AppException;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RefreshTokenService.class, RedisService.class},
        properties = {  "{security.jwt.refresh-token.expiration=1000",
                        "jwt.cookie.expiration=1000"})
class RefreshTokenServiceTest {

    @MockBean
    private RedisService redisService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private HttpServletRequest testRequest = Mockito.mock(HttpServletRequest.class);
    private HttpServletResponse testResponse = Mockito.mock(HttpServletResponse.class);

    @Test
    @DisplayName("Set refresh token in cookie and save in redis Success")
    void setRefreshTokenInCookieAndSaveInDB() {
        //given
        Integer userId = 1;
        MockHttpServletResponse response = new MockHttpServletResponse();
        //when
        refreshTokenService.setRefreshToken(userId, response);
        //then
        assertNotNull(response.getCookie("refreshToken"));
        verify(redisService).saveHashMap(anyString(), anyMap(), anyLong());
    }

    @Test
    @DisplayName("Refresh refresh token trow AppException when token not found in storage")
    void refreshRefreshTokenTrowExceptionWhenTokenNotFoundInDB() {
        //given
        Cookie[] testCookies = new Cookie[]{new Cookie("refreshToken", "refreshTokenValue")};
        when(testRequest.getCookies()).thenReturn(testCookies);
        MockHttpServletResponse response = new MockHttpServletResponse();
        //when
        try {
            refreshTokenService.refreshRefreshTokenAndGetUserId(testRequest, response);
            fail("Exception not thrown when token not found in storage");
        } catch (AppException e) {
            //then
            assertEquals("Refresh token not found in server", e.getMessage());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }
    }

    @Test
    @DisplayName("Refresh refresh token Success")
    void refreshRefreshTokenSuccess() {
        //given
        Cookie[] testCookies = new Cookie[]{new Cookie("refreshToken", "refreshTokenValue")};
        when(testRequest.getCookies()).thenReturn(testCookies);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("userId", "1");
        tokenInfo.put("revoked", "false");
        when(redisService.getHashMap("refreshTokenValue")).thenReturn(tokenInfo);
        Set<String> tokens = new HashSet<>(List.of("refreshTokenValue"));
        when(redisService.getSet("1")).thenReturn(tokens);
        //when
        refreshTokenService.refreshRefreshTokenAndGetUserId(testRequest, response);
        //then
        assertNotNull(response.getCookie("refreshToken"));
        verify(redisService).saveHashMap(anyString(), anyMap(), anyLong());
        verify(redisService).saveSet(anyString(), anySet());
    }

    @Test
    @DisplayName("Using revoked refresh token delete all tokens for user and trow AppException")
    void usingRevokedRefreshTokenDeleteAllTokensForUserAndTrowException() {
        //given
        Cookie[] testCookies = new Cookie[]{new Cookie("refreshToken", "refreshTokenValue")};
        when(testRequest.getCookies()).thenReturn(testCookies);
        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("userId", "1");
        tokenInfo.put("revoked", "true");
        when(redisService.getHashMap("refreshTokenValue")).thenReturn(tokenInfo);
        Set<String> tokens = new HashSet<>(List.of("refreshTokenValue"));
        when(redisService.getSet("1")).thenReturn(tokens);
        //when
        try {
            refreshTokenService.refreshRefreshTokenAndGetUserId(testRequest, testResponse);
            fail("Exception not thrown when using revoked token");
        } catch (AppException e) {
            //then
            assertEquals("Using revoked token", e.getMessage());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }
        verify(redisService).deleteByKey("refreshTokenValue");
    }

    @Test
    @DisplayName("Revoke user refresh tokens Success")
    void revokeUserRefreshTokensSuccess() {
        //given
        String userId = "1";
        Set<String> tokens = new HashSet<>(List.of("one", "two"));
        when(redisService.getSet(userId)).thenReturn(tokens);

        Map<String, String> tokenOneInfo = new HashMap<>();
        tokenOneInfo.put("userId", userId);
        tokenOneInfo.put("revoked", "false");
        tokenOneInfo.put("token", "one");
        when(redisService.getHashMap("one")).thenReturn(tokenOneInfo);

        Map<String, String> tokenTwoInfo = new HashMap<>();
        tokenTwoInfo.put("userId", userId);
        tokenTwoInfo.put("revoked", "false");
        tokenTwoInfo.put("token", "two");
        when(redisService.getHashMap("two")).thenReturn(tokenTwoInfo);

        //when
        refreshTokenService.revokeUserRefreshTokens(userId);
        //then
        tokenOneInfo.put("revoked", "true");
        tokenTwoInfo.put("revoked", "true");

        verify(redisService).saveHashMap("one", tokenOneInfo, 1000L);
        verify(redisService).saveHashMap("two", tokenTwoInfo, 1000L);
    }
}
