package ua.flowerista.shop.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.flowerista.shop.dto.AddressDto;

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
	private int phoneNumber;
	private AddressDto address;
	@JsonIgnore
	private String role;
	@JsonProperty("access_token")
	private String accessToken;

}
