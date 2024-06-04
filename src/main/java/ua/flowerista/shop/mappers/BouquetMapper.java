package ua.flowerista.shop.mappers;

import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.flowerista.shop.dto.BouquetDto;
import ua.flowerista.shop.dto.admin.BouquetFullDto;
import ua.flowerista.shop.models.Bouquet;
import ua.flowerista.shop.models.BouquetSize;
import ua.flowerista.shop.models.Size;
import ua.flowerista.shop.models.textContent.Languages;
import ua.flowerista.shop.models.textContent.TextContent;
import ua.flowerista.shop.models.textContent.Translation;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {FlowerMapper.class, ColorMapper.class, BouquetSizeMapper.class, TextContentMapper.class})
public interface BouquetMapper {
    Logger logger = LoggerFactory.getLogger(BouquetMapper.class);

    @Mapping(target = "name", qualifiedByName = "TextContentToStringInBouquetMapper")
    @Mapping(source = "entity.sizes", target = "defaultPrice", qualifiedByName = "SizesToDefaultPrice")
    @Mapping(source = "entity.sizes", target = "discount", qualifiedByName = "SizesToDiscount")
    @Mapping(source = "entity.sizes", target = "discountPrice", qualifiedByName = "SizesToDiscountPrice")
    @Mapping(source = "availableQuantity", target = "stockQuantity")
    BouquetDto toDto(Bouquet entity, @Context Languages language);

    List<BouquetDto> toDto(List<Bouquet> bouquets, @Context Languages language);

    @Named("TextContentToStringInBouquetMapper")
    default String getNameFromTextContext(TextContent textContent, @Context Languages language) {
        return textContent.getTranslation().stream()
                .filter((t) -> t.getTranslationEmbeddedId().getLanguage().getName().equals(language.name()))
                .findFirst()
                .map(Translation::getText)
                .orElse(textContent.getOriginalText());
    }

    @Named("SizesToDiscount")
    default BigInteger getDiscountFromMediumSize(Set<BouquetSize> sizes) {
        return sizes.stream()
                .filter((s) -> s.getSize().equals(Size.MEDIUM))
                .filter(BouquetSize::getIsSale)
                .findFirst()
                .map((s) -> s.getDefaultPrice().subtract(s.getDiscountPrice()))
                .orElse(BigInteger.ZERO);
    }

    @Named("SizesToDiscountPrice")
    default BigInteger getDiscountPriceFromMediumSize(Set<BouquetSize> sizes) {
        return sizes.stream()
                .filter((s) -> s.getSize().equals(Size.MEDIUM))
                .filter(BouquetSize::getIsSale)
                .findFirst()
                .map(BouquetSize::getDiscountPrice)
                .orElse(BigInteger.ZERO);
    }

    @Named("SizesToDefaultPrice")
    default BigInteger getDefaultPriceFromMediumSize(Set<BouquetSize> sizes) {
        return sizes.stream()
                .filter((s) -> s.getSize().equals(Size.MEDIUM))
                .findFirst()
                .map(BouquetSize::getDefaultPrice)
                .orElseGet(() -> {
                    logger.error("Medium size not found. Sizes: {}", sizes);
                    return BigInteger.ZERO;
                });
    }

    Bouquet toEntity(BouquetFullDto bouquetFullDto);

    @AfterMapping
    default void linkSizes(@MappingTarget Bouquet bouquet) {
        bouquet.getSizes().forEach(size -> size.setBouquet(bouquet));
    }

    BouquetFullDto toDto(Bouquet bouquet);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Bouquet partialUpdate(BouquetFullDto bouquetFullDto, @MappingTarget Bouquet bouquet);
}
