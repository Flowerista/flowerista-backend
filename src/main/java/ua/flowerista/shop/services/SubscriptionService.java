package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.dto.SubscriptionDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.models.Subscription;
import ua.flowerista.shop.repositories.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MailService mailService;

    public void sub(SubscriptionDto request) {
        if (subscriptionRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already exists", HttpStatus.BAD_REQUEST);
        }
        Subscription sub = new Subscription();
        sub.setEmail(request.getEmail());
        subscriptionRepository.save(sub);
        mailService.sendSuccessfulSubscription(request.getEmail());
    }
}
