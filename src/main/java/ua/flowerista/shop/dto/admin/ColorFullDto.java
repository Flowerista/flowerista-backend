package ua.flowerista.shop.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link ua.flowerista.shop.models.Color}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorFullDto implements Serializable {
    Integer id;
    TextContentDto name;
}
