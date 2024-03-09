package ua.flowerista.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * DTO for {@link ua.flowerista.shop.models.OrderItem}
 */
@Data
@NoArgsConstructor
public class OrderItemDto implements Serializable {
    Integer productId;
    @NotBlank
    String name;
    Integer quantity;
    Integer sizeId;
    @PositiveOrZero(message = "Price must be not less than 0")
    BigInteger price;
}
