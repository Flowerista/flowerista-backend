package ua.flowerista.shop.dto.admin;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link ua.flowerista.shop.models.Flower}
 */
//@Value
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FlowerFullDto implements Serializable {
    Integer id;
    TextContentDto name;
}
