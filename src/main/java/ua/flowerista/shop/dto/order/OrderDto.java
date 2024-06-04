package ua.flowerista.shop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.flowerista.shop.dto.AddressDto;
import ua.flowerista.shop.dto.user.UserDto;
import ua.flowerista.shop.models.order.Order;
import ua.flowerista.shop.models.order.OrderStatus;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;
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
    private BigInteger sum;
    private Set<OrderItemDto> orderItems;
    private AddressDto address;
    private UserDto user;
    private Instant created;
    private Instant updated;
}
