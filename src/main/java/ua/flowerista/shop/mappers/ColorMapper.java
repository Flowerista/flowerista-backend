package ua.flowerista.shop.mappers;

import org.springframework.stereotype.Component;

import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.models.Color;

@Component
public class ColorMapper implements EntityMapper<Color, ColorDto> {

	@Override
	public Color toEntity(ColorDto dto) {
		Color entity = new Color();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		return entity;
	}

	@Override
	public ColorDto toDto(Color entity) {
		ColorDto dto = new ColorDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		return dto;
	}

}
