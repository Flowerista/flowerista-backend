package ua.flowerista.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link ua.flowerista.shop.models.Flower}
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FlowerDto {

    private int id;
    private String name;

    public FlowerDto(String name) {
        this.name = name;
    }
}
