package ua.flowerista.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ua.flowerista.shop.services.UserService;

@Controller
public class TokenVerificationController {

	@Autowired
	UserService service;

	@GetMapping("/registrationConfirm")
	public String registrationConfirm(@RequestParam("token") final String token) {
		final String tokenValidated = service.validateVerificationToken(token);
		if (tokenValidated.equals("invalidToken")) {
			return "redirect://flowerista-frontend.vercel.app/";
		}
		if (tokenValidated.equals("expired")) {
			return "redirect://flowerista-frontend.vercel.app/";
		}
		return "redirect://flowerista-frontend.vercel.app/login";
	}

}
