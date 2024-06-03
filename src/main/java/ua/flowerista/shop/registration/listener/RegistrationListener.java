package ua.flowerista.shop.registration.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ua.flowerista.shop.registration.OnRegistrationCompleteEvent;
import ua.flowerista.shop.services.UserService;

@Component
@RequiredArgsConstructor
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final UserService userService;

    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        userService.sendRegistrationVerificationEmail(event.getUser(), event.getLocale(), event.getAppUrl());
    }

}
