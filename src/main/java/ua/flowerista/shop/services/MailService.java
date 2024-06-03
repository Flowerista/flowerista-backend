package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.User;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final MessageSource messages;

    @Value("${spring.mail.username}")
    private String supportEmail;
    @Value("${frontend.server.url}")
    private String frontendServerUrl;

    public void sendRegistrationVerificationEmail(User user,
                                                  String token,
                                                  String appUrl,
                                                  Locale locale) {
        String subject = "Flowerista Registration Confirmation";
        String confirmationUrl = appUrl + "/registrationConfirm?token=" + token;
        String message = messages.getMessage(
                "message.regSuccLink",
                null, "Dear " + user.getFirstName() + ", you have been registered successfully. " +
                        "To confirm your registration, please click on the below link (link is valid only 24 hours).",
                locale) + "\r\n" + confirmationUrl;
        mailSender.send(constructEmail(subject, message, user.getEmail()));
    }

    public void sendResetPasswordEmail(User user,
                                       String token) {
        String resetPasswordUrl = frontendServerUrl + "/changePassword?token=" + token;
        String message = "To reset your password, follow the link (link is valid only 24 hours)\r\n" + resetPasswordUrl;
        String subject = "Flowerista Reset Password";
        mailSender.send(constructEmail(subject, message, user.getEmail()));
    }

    public void sendSuccessfulSubscription(String email) {
        String subject = "Successful subscription";
        String message = "Subscription successfully connected! In the future, information about the latest offers will " +
                "be sent to your email! Thank you for choosing us!";
        mailSender.send(constructEmail(subject, message, email));
    }

    private SimpleMailMessage constructEmail(String subject, String body, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(body);
        message.setTo(email);
        message.setFrom(supportEmail);
        return message;
    }
}
