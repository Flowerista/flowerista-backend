package ua.flowerista.shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.flowerista.shop.dto.order.OrderDto;
import ua.flowerista.shop.models.order.Order;
import ua.flowerista.shop.services.UserService;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, AddressMapper.class, UserMapper.class, UserService.class})
public interface OrderMapper {
    @Mapping(source = "userId", target = "user")
    Order toEntity(OrderDto dto);

    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order entity);

    List<OrderDto> toDto(List<Order> entities);
}
