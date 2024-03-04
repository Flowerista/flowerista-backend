package ua.flowerista.shop.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.flowerista.shop.dto.OrderItemDto;
import ua.flowerista.shop.models.OrderItem;
import ua.flowerista.shop.repo.BouqueteRepository;
import ua.flowerista.shop.repo.BouqueteSizeRepository;
import ua.flowerista.shop.repo.ColorRepository;

@Component
@RequiredArgsConstructor
public class OrderItemMapper implements EntityMapper<OrderItem, OrderItemDto> {
    private final ColorRepository colorRepository;
    private final BouqueteSizeRepository bouqueteSizeRepository;
    private final BouqueteRepository bouqueteRepository;

    @Override
    public OrderItem toEntity(OrderItemDto dto) {
        OrderItem entity = new OrderItem();
        entity.setId(dto.getId());
        entity.setBouquete(bouqueteRepository.findById(dto.getProductId()));
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        //TODO: change get() to orElseThrow()
        entity.setColor(colorRepository.findById(dto.getColorId()).get());
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
        dto.setId(entity.getId());
        dto.setProductId(entity.getBouquete().getId());
        dto.setName(entity.getName());
        dto.setQuantity(entity.getQuantity());
        dto.setColorId(entity.getColor().getId());
        dto.setSizeId(entity.getSize().getId());
        dto.setPrice(entity.getPrice());
        return dto;
    }
}
