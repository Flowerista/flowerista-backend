package ua.flowerista.shop.dto.user;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResetPasswordDto {

	@Pattern(
		    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		    message = "Password must be at least 8 characters long, contain a mix of uppercase and lowercase letters, " +
                    "include at least one numerical digit, and include at least one special character."
		)
	private String password;

	private String token;

	@Pattern(
		    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		    message = "Password must be at least 8 characters long, contain a mix of uppercase and lowercase letters, " +
                    "include at least one numerical digit, and include at least one special character."
		)
	private String passwordRepeated;
}
