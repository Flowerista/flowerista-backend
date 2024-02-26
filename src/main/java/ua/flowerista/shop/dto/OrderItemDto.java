package ua.flowerista.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link ua.flowerista.shop.models.OrderItem}
 */
@Data
@NoArgsConstructor
public class OrderItemDto implements Serializable {
    Integer id;
    private int productId;
    @NotBlank
    String name;
    int quantity;
    int sizeId;
    int colorId;
    @PositiveOrZero(message = "Price must be not less than 0")
    int price;
}
