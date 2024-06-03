package ua.flowerista.shop.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.services.RefreshTokenService;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        refreshTokenService.logout(request, response);
    }
}
