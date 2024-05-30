package ua.flowerista.shop.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

	private int id;

	@Size(min = 2, message = "Firstname is too short")
	@Size(max = 50, message = "Firstname is too long")
	@Pattern.List({
			@Pattern(regexp = "^[\\p{Alpha}]*$", message = "Firstname should contain only alphabets"),
			@Pattern(regexp = "^[^\\s].*$", message = "Firstname should not start with space"),
			@Pattern(regexp = "^.*[^\\s]$", message = "Firstname should not end with space"),
			@Pattern(regexp = "^((?!  ).)*$", message = "Firstname should not contain consecutive spaces"),
			@Pattern(regexp = "^[^a-z].*$", message = "Firstname should not start with a lower case character") })
	private String firstName;

	@Size(min = 2, message = "Lastname is too short")
	@Size(max = 50, message = "Lastname is too long")
	@Pattern.List({
			@Pattern(regexp = "^[\\p{Alpha}]*$", message = "Lastname should contain only alphabets"),
			@Pattern(regexp = "^[^\\s].*$", message = "Lastname should not start with space"),
			@Pattern(regexp = "^.*[^\\s]$", message = "Lasttname should not end with space"),
			@Pattern(regexp = "^((?!  ).)*$", message = "Lastname should not contain consecutive spaces"),
			@Pattern(regexp = "^[^a-z].*$", message = "Lastname should not start with a lower case character") })
	private String lastName;

	@Email
	private String email;

	@Size(min = 9, max = 9, message = "Phone number should be exact 9 characters.")
	private int phoneNumber;
	@JsonIgnore
	private String role;

}
