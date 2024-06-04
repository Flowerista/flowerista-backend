package ua.flowerista.shop.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.flowerista.shop.models.order.OrderItem;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;

/**
 * DTO for {@link OrderItem}
 */
@Data
@NoArgsConstructor
public class OrderItemDto implements Serializable {
    Integer id;
    Integer productId;
    @NotBlank
    String name;
    Integer quantity;
    Integer sizeId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String size;
    @PositiveOrZero(message = "Price must be not less than 0")
    BigInteger price;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Integer, String> imageUrls;
}
