package ua.flowerista.shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	@Pattern(regexp = "^[\\p{L}\\p{IsCyrillic} -]*$", message = "Only letters, spaces, hyphens, and combinations are allowed in the city name")
	private String city;
	@NotBlank(message = "Street name must not be empty")
	@Pattern(regexp = "^[\\p{L}\\p{IsCyrillic} -]*$", message = "Only letters, spaces, hyphens, and combinations are allowed in the street name")
	private String street;
	@NotBlank(message = "House number must not be empty")
	@Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}0-9/]+$", message = "Only letters, numbers, '/', and combinations are allowed in the house number")
	private String house;
	@Pattern(regexp = "^[0-9]*$", message = "Only numbers are allowed in the entrance")
	private String entrance;
	@Pattern(regexp = "^[0-9]*$", message = "Only numbers are allowed in the flat")
	private String flat;
	@JsonProperty("date")
	private String dateDelivery;
	@JsonProperty("time")
	private String timeDelivery;

	@Override
	public String toString() {
		return "city='" + city + '\'' +
				", street='" + street + '\'' +
				", house='" + house + '\'' +
				", entrance='" + entrance + '\'' +
				", flat='" + flat;
	}
}
