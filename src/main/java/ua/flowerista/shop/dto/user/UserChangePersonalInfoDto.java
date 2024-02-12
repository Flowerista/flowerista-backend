package ua.flowerista.shop.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChangePersonalInfoDto {

	@Size(min = 2, message = "Firstname is too short")
	@Size(max = 50, message = "Firstname is too long")
	@Pattern(regexp = "^[\\p{L}-]*$", message = "Firstname should contain only letters")
	@Pattern(regexp = "^[A-Z\\p{IsCyrillic}].*", message = "Firstname must start with an uppercase letter")
	private String firstName;
	@Size(min = 2, message = "Lastname is too short")
	@Size(max = 50, message = "Lastname is too long")
	@Pattern(regexp = "^[\\p{L}-]*$", message = "Lastname should contain only letters")
	@Pattern(regexp = "^[A-Z\\p{IsCyrillic}].*", message = "Lastname must start with an uppercase letter")
	private String lastName;
}
