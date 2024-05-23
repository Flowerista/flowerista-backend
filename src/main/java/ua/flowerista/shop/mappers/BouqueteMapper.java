package ua.flowerista.shop.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.flowerista.shop.dto.BouqueteCardDto;
import ua.flowerista.shop.dto.BouqueteDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.models.*;

@Component
public class BouqueteMapper implements EntityMapper<Bouquete, BouqueteDto>, EntityMultiLanguagesDtoMapper<Bouquete, BouqueteDto> {

	private ColorMapper colorMapper;
	private FlowerMapper flowerMapper;


	@Autowired
	public BouqueteMapper(ColorMapper colorMapper, FlowerMapper flowerMapper) {
		this.colorMapper = colorMapper;
		this.flowerMapper = flowerMapper;
	}

	@Override
	public Bouquete toEntity(BouqueteDto dto) {
		return null;
	}

	@Override
	public BouqueteDto toDto(Bouquete entity) {
		BouqueteDto dto = new BouqueteDto();
		dto.setId(entity.getId());
		dto.setFlowers(
				entity.getFlowers().stream().map(flower -> flowerMapper.toDto(flower)).collect(Collectors.toSet()));
		dto.setColors(entity.getColors().stream().map(color -> colorMapper.toDto(color)).collect(Collectors.toSet()));
		dto.setItemCode(entity.getItemCode());
		dto.setName(entity.getName());
		dto.setQuantity(entity.getQuantity());
		dto.setImageUrls(entity.getImageUrls());
		dto.setSoldQuantity(entity.getSoldQuantity());
		dto.setSizes(entity.getSizes());
		return dto;
	}

	public BouqueteSmallDto toSmallDto(Bouquete entity) {
		BouqueteSmallDto dto = new BouqueteSmallDto();
		BouqueteSize size = findBouqueteSize(entity);
		dto.setId(entity.getId());
		dto.setDefaultPrice(size.getDefaultPrice());
		dto.setDiscount(size.getDiscount());
		dto.setDiscountPrice(size.getDiscountPrice());
		dto.setName(entity.getName());
		dto.setImageUrls(entity.getImageUrls());
		dto.setSizes(entity.getSizes());
		dto.setStockQuantity(entity.getQuantity());
		return dto;
	}

	public BouqueteCardDto toCardDto(Bouquete entity) {
		BouqueteCardDto dto = new BouqueteCardDto();
		dto.setId(entity.getId());
		dto.setFlowers(entity.getFlowers().stream().map(flower -> flowerMapper.toDto(flower)).collect(Collectors.toSet()));
		dto.setImageUrls(entity.getImageUrls());
		dto.setItemCode(entity.getItemCode());
		dto.setName(entity.getName());
		dto.setSizes(entity.getSizes());
		dto.setStockQuantity(entity.getQuantity());
		return dto;
	}

	private BouqueteSize findBouqueteSize(Bouquete bouquete) {
		Set<BouqueteSize> sizes = bouquete.getSizes();
		for (BouqueteSize bouqueteSize : sizes) {
			if (bouqueteSize.getSize() == Size.MEDIUM) {
				return bouqueteSize;
			}
		}
		throw new RuntimeException("Size not found for Bouquete: " + bouquete.getId());
	}

	@Override
	public BouqueteDto toDto(Bouquete entity, Languages language) {
		BouqueteDto dto = new BouqueteDto();
		dto = mapGenericField(entity, dto);
		dto = translateFields(entity, dto, language);
		return dto;
	}

	public BouqueteSmallDto toSmallDto(Bouquete entity, Languages language) {
		BouqueteDto dto = toDto(entity, language);

		BouqueteSmallDto bouqueteSmallDto = new BouqueteSmallDto();
		BouqueteSize size = findBouqueteSize(entity);
		bouqueteSmallDto.setId(dto.getId());
		bouqueteSmallDto.setDefaultPrice(size.getDefaultPrice());
		bouqueteSmallDto.setDiscount(size.getDiscount());
		bouqueteSmallDto.setDiscountPrice(size.getDiscountPrice());
		bouqueteSmallDto.setName(dto.getName());
		bouqueteSmallDto.setImageUrls(dto.getImageUrls());
		bouqueteSmallDto.setSizes(dto.getSizes());
		bouqueteSmallDto.setStockQuantity(dto.getQuantity());
		return bouqueteSmallDto;
	}

	public BouqueteCardDto toCardDto(Bouquete entity, Languages language) {
		BouqueteDto dto = toDto(entity, language);

		BouqueteCardDto bouqueteCardDto = new BouqueteCardDto();
		bouqueteCardDto.setId(dto.getId());
		bouqueteCardDto.setFlowers(dto.getFlowers());
		bouqueteCardDto.setImageUrls(dto.getImageUrls());
		bouqueteCardDto.setItemCode(dto.getItemCode());
		bouqueteCardDto.setName(dto.getName());
		bouqueteCardDto.setSizes(dto.getSizes());
		bouqueteCardDto.setStockQuantity(dto.getQuantity());
		return bouqueteCardDto;
	}

	private BouqueteDto mapGenericField(Bouquete entity, BouqueteDto dto) {
		dto.setId(entity.getId());
		dto.setItemCode(entity.getItemCode());
		dto.setQuantity(entity.getQuantity());
		dto.setImageUrls(entity.getImageUrls());
		dto.setSizes(entity.getSizes());
		dto.setSoldQuantity(entity.getSoldQuantity());
		return dto;
	}
	private BouqueteDto translateFields(Bouquete entity, BouqueteDto dto, Languages language) {
		//choose color
		dto.setColors(entity.getColors().stream()
				.map(color -> colorMapper.toDto(color, language))
				.collect(Collectors.toSet()));
		//choose name
		dto.setName(entity.getTranslates().stream()
				.filter((t) -> t.getLanguage() == language)
				.findFirst()
				.orElse(Translate.builder().text(entity.getName()).build())
				.getText());
		//choose flowers
		dto.setFlowers(entity.getFlowers().stream()
				.map(flower -> flowerMapper.toDto(flower, language))
				.collect(Collectors.toSet()));
		return dto;
	}


}
