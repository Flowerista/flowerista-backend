package ua.flowerista.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link Order}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto implements Serializable {
    private Integer id;
    private OrderStatus status;
    private String payId;
    private Integer userId;
    private BigDecimal sum;
    private Set<OrderItemDto> orderItems;
}
