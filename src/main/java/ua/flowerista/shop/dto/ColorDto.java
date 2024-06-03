package ua.flowerista.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link ua.flowerista.shop.models.Color}
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ColorDto {

    private int id;
    private String name;

}
