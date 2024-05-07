package ua.flowerista.shop.mappers;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.flowerista.shop.dto.BouqueteCardDto;
import ua.flowerista.shop.dto.BouqueteDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.models.*;

@Component
public class BouqueteMapper implements EntityMapper<Bouquete, BouqueteDto>, EntityMultiLanguagesMapper<Bouquete, BouqueteDto> {

	private ColorMapper colorMapper;
	private FlowerMapper flowerMapper;


	@Autowired
	public BouqueteMapper(ColorMapper colorMapper, FlowerMapper flowerMapper) {
		this.colorMapper = colorMapper;
		this.flowerMapper = flowerMapper;
	}

	@Override
	public Bouquete toEntity(BouqueteDto dto) {
		Bouquete entity = new Bouquete();

		entity.setId(dto.getId());
		entity.setSizes(dto.getSizes());
		entity.setFlowers(dto.getFlowers().stream().map(flowerDto -> flowerMapper.toEntity(flowerDto))
				.collect(Collectors.toSet()));
		entity.setColors(
				dto.getColors().stream().map(colorDto -> colorMapper.toEntity(colorDto)).collect(Collectors.toSet()));
		entity.setItemCode(dto.getItemCode());
		entity.setName(dto.getName());
		entity.setQuantity(dto.getQuantity());
		entity.setSoldQuantity(dto.getSoldQuantity());
		entity.setImageUrls(dto.getImageUrls());
		return entity;
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
		dto.setFlowers(entity.getFlowers());
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

	//TODO: implement
	//Will be implemented in the future
	@Override
	public Bouquete toEntity(BouqueteDto dto, Languages language) {
		return null;
	}

	@Override
	public BouqueteDto toDto(Bouquete entity, Languages language) {
		BouqueteDto dto = new BouqueteDto();
		dto.setId(entity.getId());
		dto.setFlowers(
				entity.getFlowers().stream().map(flower -> flowerMapper.toDto(flower)).collect(Collectors.toSet()));
		dto.setColors(entity.getColors().stream().map(color -> colorMapper.toDto(color, language)).collect(Collectors.toSet()));
		dto.setItemCode(entity.getItemCode());
		dto.setName(entity.getTranslates().stream()
				.filter((t) -> t.getLanguage() == language)
				.findFirst()
				.get()
				.getText());
		dto.setQuantity(entity.getQuantity());
		dto.setImageUrls(entity.getImageUrls());
		dto.setSoldQuantity(entity.getSoldQuantity());
		dto.setSizes(entity.getSizes());
		return dto;
	}

	public BouqueteSmallDto toSmallDto(Bouquete entity, Languages language) {
		BouqueteSmallDto dto = new BouqueteSmallDto();
		BouqueteSize size = findBouqueteSize(entity);
		dto.setId(entity.getId());
		dto.setDefaultPrice(size.getDefaultPrice());
		dto.setDiscount(size.getDiscount());
		dto.setDiscountPrice(size.getDiscountPrice());
		dto.setName(entity.getTranslates().stream()
				.filter((t) -> t.getLanguage() == language)
				.findFirst()
				.get()
				.getText());
		dto.setImageUrls(entity.getImageUrls());
		dto.setSizes(entity.getSizes());
		dto.setStockQuantity(entity.getQuantity());
		return dto;
	}

	public BouqueteCardDto toCardDto(Bouquete entity, Languages language) {
		BouqueteCardDto dto = new BouqueteCardDto();
		dto.setId(entity.getId());
		dto.setFlowers(entity.getFlowers());
		dto.setImageUrls(entity.getImageUrls());
		dto.setItemCode(entity.getItemCode());
		dto.setName(entity.getTranslates().stream()
				.filter((t) -> t.getLanguage() == language)
				.findFirst()
				.get()
				.getText());
		dto.setSizes(entity.getSizes());
		dto.setStockQuantity(entity.getQuantity());
		return dto;
	}
}
