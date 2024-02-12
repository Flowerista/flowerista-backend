package ua.flowerista.shop.mappers;

import org.springframework.stereotype.Component;

import ua.flowerista.shop.dto.AddressDto;
import ua.flowerista.shop.models.Address;

@Component
public class AddressMapper implements EntityMapper<Address, AddressDto> {

	@Override
	public Address toEntity(AddressDto dto) {
		Address entity = new Address();
		entity.setCity(dto.getCity());
		entity.setStreet(dto.getStreet());
		entity.setHouse(dto.getHouse());
		entity.setEntrance(dto.getEntrance());
		entity.setFlat(dto.getFlat());
		return entity;
	}

	@Override
	public AddressDto toDto(Address entity) {
		AddressDto dto = new AddressDto();
		dto.setCity(entity.getCity());
		dto.setStreet(entity.getStreet());
		dto.setHouse(entity.getHouse());
		dto.setEntrance(entity.getEntrance());
		dto.setFlat(entity.getFlat());
		return dto;
	}

}
