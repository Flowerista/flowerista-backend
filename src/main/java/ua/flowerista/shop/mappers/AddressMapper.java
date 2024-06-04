package ua.flowerista.shop.mappers;

import org.mapstruct.Mapper;
import ua.flowerista.shop.dto.AddressDto;
import ua.flowerista.shop.models.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressDto dto);

    AddressDto toDto(Address entity);
}
