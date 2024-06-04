package ua.flowerista.shop.configs;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.flowerista.shop.dto.user.UserDto;
import ua.flowerista.shop.models.user.Role;
import ua.flowerista.shop.models.user.User;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UserAuthenticationProvider.class, UserDetailsService.class},
        properties = {"security.jwt.secret-key=secret-test-key", "security.jwt.expiration=10000"})
class UserAuthenticationProviderTest {

    @MockBean
    private UserDetailsService userDetailsService;
    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Test
    @DisplayName("Should create valid access token with claims")
    void shouldCreateValidAccessTokenWithClaims() {
        //given
        UserDto user = UserDto.builder()
                .id(1)
                .email("test@test.com")
                .role(Role.USER)
                .build();
        //when
        String token = userAuthenticationProvider.createAccessToken(user);
        //then
        DecodedJWT decodedJwt = getDecodedJwt(token);
        Integer claimId = decodedJwt.getClaim("id").asInt();
        String claimRole = decodedJwt.getClaim("role").asString();
        assertNotNull(token, "Token should not be null");
        assertEquals(user.getId(), claimId, "Claim id should be equal to user id");
        assertEquals(user.getRole().name(), claimRole, "Claim role should be equal to user role");
    }

    @Test
    @DisplayName("Should throw exception when token is invalid on validate")
    void shouldThrowExceptionWhenTokenIsInvalidAndValidate() {
        //given
        String token = "invalidToken";
        //when
        try {
            userAuthenticationProvider.validateToken(token);
            fail("Exception should be thrown");
        } catch (Exception e) {
            //then
            assertNotNull(e, "Exception should be thrown");
        }
    }

    @Test
    @DisplayName("Should throw exception when token is invalid on validate strongly")
    void shouldThrowExceptionWhenTokenIsInvalidAndValidateStrongly() {
        //given
        String token = "invalidToken";
        //when
        try {
            userAuthenticationProvider.validateTokenStrongly(token);
            fail("Exception should be thrown");
        } catch (Exception e) {
            //then
            assertNotNull(e, "Exception should be thrown");
        }
    }

    @Test
    @DisplayName("Should return correct authentication for validate token with user ID in object")
    void ShouldReturnCorrectAuthenticationForValidateToken() {
        //given
        UserDto user = UserDto.builder()
                .id(1)
                .email("test@test.com")
                .role(Role.USER)
                .build();
        String token = userAuthenticationProvider.createAccessToken(user);
        //when
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) userAuthenticationProvider.validateToken(token);
        //then
        assertNotNull(authentication, "Authentication should not be null");
        assertEquals(user.getId(), Integer.valueOf(authentication.getPrincipal().toString()),
                "Principal should be equal to user id");
    }

    @Test
    @DisplayName("Should return correct authentication for validate token with user full object")
    void ShouldReturnCorrectAuthenticationForValidateTokenStrongly() {
        //given
        UserDto userDto = UserDto.builder()
                .id(1)
                .email("test@test.com")
                .role(Role.USER)
                .build();
        String token = userAuthenticationProvider.createAccessToken(userDto);
        User user = User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .role(Role.USER)
                .build();
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(user);
        //when
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) userAuthenticationProvider.validateTokenStrongly(token);
        User principal = (User) authentication.getPrincipal();
        //then
        assertNotNull(authentication, "Authentication should not be null");
        assertNotNull(principal, "Principal should not be null");
        assertEquals(userDto.getId(), principal.getId(),"Principal should be equal to user id");
        assertEquals(userDto.getEmail(), principal.getEmail(),"Principal should be equal to user email");
        assertEquals(userDto.getRole().name(), principal.getRole().name(),"Principal should be equal to user role");
    }

    private DecodedJWT getDecodedJwt(String token) {
        String secretKey = Base64.getEncoder().encodeToString("secret-test-key".getBytes());
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
