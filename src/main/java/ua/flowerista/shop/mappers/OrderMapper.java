package ua.flowerista.shop.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.flowerista.shop.dto.OrderDto;
import ua.flowerista.shop.models.Order;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper implements EntityMapper<Order, OrderDto>{
    private final OrderItemMapper orderItemMapper;
    @Override
    public Order toEntity(OrderDto dto) {
        Order entity = new Order();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setPayId(dto.getPayId());
        entity.setUserId(dto.getUserId());
        entity.setSum(dto.getSum());
        entity.setOrderItems(dto.getOrderItems().stream()
                .map(orderItemDto -> orderItemMapper.toEntity(orderItemDto))
                .collect(Collectors.toSet()));
        return entity;
    }

    @Override
    public OrderDto toDto(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setPayId(entity.getPayId());
        dto.setUserId(entity.getUserId());
        dto.setSum(entity.getSum());
        dto.setOrderItems(entity.getOrderItems().stream()
                .map(orderItem -> orderItemMapper.toDto(orderItem))
                .collect(Collectors.toSet()));
        return dto;
    }
}
