package ua.flowerista.shop.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class VerificationToken {
    private String id;
    private String userLogin;

    public VerificationToken(String userLogin) {
        this.id = UUID.randomUUID().toString();
        this.userLogin = userLogin;
    }
}
