package ua.flowerista.shop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.flowerista.shop.dto.AddressDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private AddressDto address;
}
