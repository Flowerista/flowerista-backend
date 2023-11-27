package ua.flowerista.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddressDto {
	@NotBlank(message = "City name must not be empty")
	@Pattern(regexp = "^\\p{L}*$", message = "Only letters are allowed in the city name")
	private String city;
	@NotBlank(message = "Street name must not be empty")
	@Pattern(regexp = "^\\p{L}*$", message = "Only letters are allowed in the street name")
	private String street;
	@NotBlank(message = "House number must not be empty")
	@Pattern(regexp = "^[a-zA-Z0-9/]+$", message = "Only letters, numbers, '/', and combinations are allowed in the house number")
	private String house;
	@Pattern(regexp = "^[0-9]*$", message = "Only numbers are allowed in the entrance")
	private String entrance;
	@Pattern(regexp = "^[0-9]*$", message = "Only numbers are allowed in the flat")
	private String flat;
}
