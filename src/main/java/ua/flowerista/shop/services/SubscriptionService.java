package ua.flowerista.shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.flowerista.shop.dto.SubscriptionRequest;
import ua.flowerista.shop.models.Subscription;
import ua.flowerista.shop.repo.SubscriptionRepository;

@Service
public class SubscriptionService {
	
	@Autowired
	private SubscriptionRepository repo;
	
	
	public String sub (SubscriptionRequest request) {
		if (repo.existsByEmail(request.getEmail())) {
			return "Email already exist";
		}
		Subscription sub = new Subscription();
		sub.setEmail(request.getEmail());
		repo.save(sub);
		return "Email added";
	}

}
