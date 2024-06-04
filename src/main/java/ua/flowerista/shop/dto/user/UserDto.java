package ua.flowerista.shop.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ua.flowerista.shop.dto.AddressDto;
import ua.flowerista.shop.models.user.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private AddressDto address;
	@JsonIgnore
	private Role role;
	@JsonProperty("access_token")
	private String accessToken;

}
