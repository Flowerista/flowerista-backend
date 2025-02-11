package ua.flowerista.shop.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAuthenticationResponseDto {

	  @JsonProperty("access_token")
	  private String accessToken;
	  @JsonIgnore
	  private String refreshToken;
	  @JsonProperty("user")
	  private UserProfileDto user;

}
