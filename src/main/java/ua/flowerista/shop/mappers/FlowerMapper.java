package ua.flowerista.shop.mappers;

import org.springframework.stereotype.Component;

import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.models.Translate;

@Component
public class FlowerMapper implements EntityMapper<Flower, FlowerDto> , EntityMultiLanguagesDtoMapper<Flower, FlowerDto>{

	@Override
	public Flower toEntity(FlowerDto dto) {
		Flower entity = new Flower();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		return entity;
	}

	@Override
	public FlowerDto toDto(Flower entity) {
		FlowerDto dto = new FlowerDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		return dto;
	}

	@Override
	public FlowerDto toDto(Flower entity, Languages language) {
		FlowerDto dto = new FlowerDto();
		dto.setId(entity.getId());
		dto.setName(entity.getNameTranslate().stream()
				.filter((t) -> t.getLanguage() == language)
				.findFirst()
				.orElse(Translate.builder().text(entity.getName()).build())
				.getText());
		return dto;
	}
}
