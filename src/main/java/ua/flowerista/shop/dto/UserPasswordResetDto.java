package ua.flowerista.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserPasswordResetDto {

    private String password;

    private  String token;

    private String passwordRepeated;
}
