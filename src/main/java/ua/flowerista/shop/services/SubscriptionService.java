package ua.flowerista.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import ua.flowerista.shop.dto.SubscriptionRequest;
import ua.flowerista.shop.models.Subscription;
import ua.flowerista.shop.repo.SubscriptionRepository;

@Service
public class SubscriptionService {
	
	@Autowired
	private SubscriptionRepository repo;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String username;

	public String sub (SubscriptionRequest request) {
		if (repo.existsByEmail(request.getEmail())) {
			return "Email already exist";
		}
		Subscription sub = new Subscription();
		sub.setEmail(request.getEmail());
		repo.save(sub);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(username);
		mailMessage.setTo(request.getEmail());
		mailMessage.setSubject("Successful subscription");
		mailMessage.setText("Subscription successfully connected! In the future, information about the latest offers will be sent to your email! Thank you for choosing us!");
		mailSender.send(mailMessage);

		return "Email added";
	}

}
