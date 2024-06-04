package ua.flowerista.shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.flowerista.shop.dto.order.OrderItemDto;
import ua.flowerista.shop.models.order.OrderItem;
import ua.flowerista.shop.services.BouquetService;
import ua.flowerista.shop.services.BouquetSizeService;

@Mapper(componentModel = "spring", uses = {BouquetService.class, BouquetSizeService.class})
public interface OrderItemMapper {
    @Mapping(source = "productId", target = "bouquet")
    @Mapping(source = "sizeId", target = "size")
    OrderItem toEntity(OrderItemDto dto);

    @Mapping(source = "bouquet.id", target = "productId")
    @Mapping(source = "bouquet.imageUrls", target = "imageUrls")
    @Mapping(source = "size.size.", target = "size")
    @Mapping(source = "size.id", target = "sizeId")
    OrderItemDto toDto(OrderItem entity);

}
