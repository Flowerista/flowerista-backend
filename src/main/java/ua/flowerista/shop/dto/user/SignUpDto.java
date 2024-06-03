package ua.flowerista.shop.dto.user;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignUpDto {

	@Size(min = 2, message = "Firstname is too short")
	@Size(max = 50, message = "Firstname is too long")
	@Pattern(regexp = "^[\\p{L}-]*$", message = "Firstname should contain letters and hyphens")
	@Pattern(regexp = "^[A-Z\\p{IsCyrillic}].*", message = "Firstname must start with an uppercase letter")
	private String firstName;

	@Size(min = 2, message = "Lastname is too short")
	@Size(max = 50, message = "Lastname is too long")
	@Pattern(regexp = "^[\\p{L}-]*$", message = "Lastname should contain only letters")
	@Pattern(regexp = "^[A-Z\\p{IsCyrillic}].*", message = "Lastname must start with an uppercase letter")
	private String lastName;
	@Email
	private String email;
	@Min(value = 100000000, message = "Phone number should have exactly 9 digits.")
	@Digits(integer = 9, fraction = 0, message = "Phone number should have exactly 9 digits.")
	private Integer phoneNumber;
	@Pattern(
		    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		    message = "Password must be at least 8 characters long, contain a mix of uppercase and lowercase letters, include at least one numerical digit, and include at least one special character."
		)
	private String password;

}
