package ua.flowerista.shop.configs;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ua.flowerista.shop.dto.user.UserDto;
import ua.flowerista.shop.models.user.User;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationProvider.class);

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration}")
    private Long tokenExpiration;

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(UserDto user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenExpiration);

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("role", user.getRole().name())
                .withClaim("id", user.getId())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
    }

    public Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decoded = verifier.verify(token);
            return new UsernamePasswordAuthenticationToken(
                    decoded.getClaim("id"),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + decoded.getClaim("role").asString()))
            );
        } catch (JWTVerificationException e) {
            logger.error("Invalid Token: " + token + ". ", e.getMessage());
            throw e;
        }
    }

    public Authentication validateTokenStrongly(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decoded = verifier.verify(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(decoded.getSubject());
            User user = (User) userDetails;
            return new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    userDetails.getAuthorities()
            );
        } catch (JWTVerificationException e) {
            logger.error("Invalid Token: " + token + ". ", e.getMessage());
            throw e;
        }
    }


}
