package ua.flowerista.shop.mappers;

import org.springframework.stereotype.Component;

import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.models.Translate;

@Component
public class ColorMapper implements EntityMapper<Color, ColorDto>, EntityMultiLanguagesDtoMapper<Color, ColorDto>{

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

	@Override
	public ColorDto toDto(Color entity, Languages language) {
		ColorDto dto = new ColorDto();
		dto.setId(entity.getId());
		dto.setName(entity.getNameTranslate().stream()
				.filter((t) -> t.getLanguage() == language)
				.findFirst()
				.orElse(Translate.builder().text(entity.getName()).build())
				.getText());
		return dto;
	}

}
