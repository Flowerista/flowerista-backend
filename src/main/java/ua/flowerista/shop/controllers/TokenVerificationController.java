package ua.flowerista.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.flowerista.shop.services.UserService;

@Controller
@RequiredArgsConstructor
public class TokenVerificationController {

    private final UserService userService;
    @Value("${frontend.server.url}")
    private String frontendUrl;
    @GetMapping("/registrationConfirm")
    public String registrationConfirm(@RequestParam("token") final String token) {
        userService.processRegistrationToken(token);
        return "redirect://" + frontendUrl + "/login";
    }
}
