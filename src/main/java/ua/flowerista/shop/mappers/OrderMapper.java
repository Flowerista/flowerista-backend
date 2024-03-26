package ua.flowerista.shop.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.flowerista.shop.dto.OrderDto;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.services.UserService;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper implements EntityMapper<Order, OrderDto>{
    private final OrderItemMapper orderItemMapper;
    private final AddressMapper addressMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    @Override
    public Order toEntity(OrderDto dto) {
        Order entity = new Order();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setPayId(dto.getPayId());
        entity.setUser(userService.getUserById(dto.getUserId()));
        entity.setSum(dto.getSum());
        entity.setOrderItems(dto.getOrderItems().stream()
                .map(orderItemMapper::toEntity)
                .collect(Collectors.toSet()));
        entity.setAddress(addressMapper.toEntity(dto.getAddress()));
        entity.setCreated(dto.getCreated());
        if (dto.getUpdated() != null) {
            entity.setUpdated(dto.getUpdated());
        }
        return entity;
    }

    @Override
    public OrderDto toDto(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setPayId(entity.getPayId());
        dto.setUser(userMapper.toDto(entity.getUser()));
        dto.setUserId(entity.getUser().getId());
        dto.setSum(entity.getSum());
        dto.setOrderItems(entity.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet()));
        dto.setAddress(addressMapper.toDto(entity.getAddress()));
        dto.setCreated(entity.getCreated());
        dto.setUpdated(entity.getUpdated());
        return dto;
    }
}
