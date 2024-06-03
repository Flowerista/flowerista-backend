package ua.flowerista.shop.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.flowerista.shop.dto.BouqueteCardDto;
import ua.flowerista.shop.dto.BouquetDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.models.*;

@Component
public class BouquetMapper implements EntityMapper<Bouquet, BouquetDto>, EntityMultiLanguagesDtoMapper<Bouquet, BouquetDto> {

	private ColorMapper colorMapper;
	private FlowerMapper flowerMapper;


	@Autowired
	public BouquetMapper(ColorMapper colorMapper, FlowerMapper flowerMapper) {
		this.colorMapper = colorMapper;
		this.flowerMapper = flowerMapper;
	}

	@Override
	public Bouquet toEntity(BouquetDto dto) {
		return null;
	}

	@Override
	public BouquetDto toDto(Bouquet entity) {
		BouquetDto dto = new BouquetDto();
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

	public BouqueteSmallDto toSmallDto(Bouquet entity) {
		BouqueteSmallDto dto = new BouqueteSmallDto();
		BouquetSize size = findBouqueteSize(entity);
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

	public BouqueteCardDto toCardDto(Bouquet entity) {
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

	private BouquetSize findBouqueteSize(Bouquet bouquet) {
		Set<BouquetSize> sizes = bouquet.getSizes();
		for (BouquetSize bouquetSize : sizes) {
			if (bouquetSize.getSize() == Size.MEDIUM) {
				return bouquetSize;
			}
		}
		throw new RuntimeException("Size not found for Bouquete: " + bouquet.getId());
	}

	@Override
	public BouquetDto toDto(Bouquet entity, Languages language) {
		BouquetDto dto = new BouquetDto();
		dto = mapGenericField(entity, dto);
		dto = translateFields(entity, dto, language);
		return dto;
	}

	public BouqueteSmallDto toSmallDto(Bouquet entity, Languages language) {
		BouquetDto dto = toDto(entity, language);

		BouqueteSmallDto bouqueteSmallDto = new BouqueteSmallDto();
		BouquetSize size = findBouqueteSize(entity);
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

	public BouqueteCardDto toCardDto(Bouquet entity, Languages language) {
		BouquetDto dto = toDto(entity, language);

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

	private BouquetDto mapGenericField(Bouquet entity, BouquetDto dto) {
		dto.setId(entity.getId());
		dto.setItemCode(entity.getItemCode());
		dto.setQuantity(entity.getQuantity());
		dto.setImageUrls(entity.getImageUrls());
		dto.setSizes(entity.getSizes());
		dto.setSoldQuantity(entity.getSoldQuantity());
		return dto;
	}
	private BouquetDto translateFields(Bouquet entity, BouquetDto dto, Languages language) {
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
