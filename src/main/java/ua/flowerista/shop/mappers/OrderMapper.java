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
    private final AddressMapper addressMapper;
    @Override
    public Order toEntity(OrderDto dto) {
        Order entity = new Order();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setPayId(dto.getPayId());
        entity.setUserId(dto.getUserId());
        entity.setSum(dto.getSum());
        entity.setOrderItems(dto.getItems().stream()
                .map(orderItemMapper::toEntity)
                .collect(Collectors.toSet()));
        entity.setAddress(addressMapper.toEntity(dto.getAddress()));
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
        dto.setItems(entity.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet()));
        dto.setAddress(addressMapper.toDto(entity.getAddress()));
        return dto;
    }
}
