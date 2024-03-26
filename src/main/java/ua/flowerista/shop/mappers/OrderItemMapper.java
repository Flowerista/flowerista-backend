package ua.flowerista.shop.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.flowerista.shop.dto.OrderItemDto;
import ua.flowerista.shop.models.OrderItem;
import ua.flowerista.shop.repo.BouqueteRepository;
import ua.flowerista.shop.repo.BouqueteSizeRepository;

@Component
@RequiredArgsConstructor
public class OrderItemMapper implements EntityMapper<OrderItem, OrderItemDto> {
    private final BouqueteSizeRepository bouqueteSizeRepository;
    private final BouqueteRepository bouqueteRepository;

    @Override
    public OrderItem toEntity(OrderItemDto dto) {
        OrderItem entity = new OrderItem();
        entity.setBouquete(bouqueteRepository.findById(dto.getProductId()).get());
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        //TODO: change get() to orElseThrow()
        entity.setSize(bouqueteSizeRepository.findById(dto.getSizeId()).get());
        if (entity.getSize().getIsSale()) {
            entity.setPrice(entity.getSize().getDiscountPrice());
        } else {
            entity.setPrice(entity.getSize().getDefaultPrice());
        }
        return entity;
    }

    @Override
    public OrderItemDto toDto(OrderItem entity) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(entity.getBouquete().getId());
        dto.setName(entity.getName());
        dto.setQuantity(entity.getQuantity());
        dto.setSizeId(entity.getSize().getId());
        dto.setSize(entity.getSize().getSize().name());
        dto.setPrice(entity.getPrice());
        return dto;
    }
}
