package ua.flowerista.shop.mappers;

import org.mapstruct.*;
import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.dto.admin.FlowerFullDto;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.textContent.Languages;
import ua.flowerista.shop.models.textContent.TextContent;
import ua.flowerista.shop.models.textContent.Translation;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TextContentMapper.class, TranslationMapper.class})
public interface FlowerMapper {

    @Mapping(source = "name", target = "name", qualifiedByName = "TextContentToStringInFlowerMapper")
    FlowerDto toDto(Flower entity, @Context Languages language);

    List<FlowerDto> toDto(List<Flower> flowers, @Context Languages lang);

    @Named("TextContentToStringInFlowerMapper")
    default String getNameFromTextContext(TextContent textContent, @Context Languages language) {
        return textContent.getTranslation().stream()
                .filter((t) -> t.getTranslationEmbeddedId().getLanguage().getName().equals(language.name()))
                .findFirst()
                .map(Translation::getText)
                .orElse(textContent.getOriginalText());
    }

    @Mapping(source = "name.originalLanguageName", target = "name.originalLanguage.name")
    @Mapping(source = "name.originalLanguageId", target = "name.originalLanguage.id")
    Flower toEntity(FlowerFullDto flowerFullDto);


    FlowerFullDto toAdminDto(Flower flower);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Flower partialUpdate(FlowerFullDto flowerFullDto, @MappingTarget Flower flower);
}
