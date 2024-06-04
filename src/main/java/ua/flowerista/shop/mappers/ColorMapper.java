package ua.flowerista.shop.mappers;

import org.mapstruct.*;
import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.dto.admin.ColorFullDto;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.models.textContent.Languages;
import ua.flowerista.shop.models.textContent.TextContent;
import ua.flowerista.shop.models.textContent.Translation;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TextContentMapper.class, TranslationMapper.class})
public interface ColorMapper {
    @Mapping(target = "name", qualifiedByName = "TextContentToStringInColorMapper")
    ColorDto toDto(Color entity, @Context Languages language);

    List<ColorDto> toDto(List<Color> colors, @Context Languages language);

    @Named("TextContentToStringInColorMapper")
    default String getNameFromTextContext(TextContent textContent, @Context Languages language) {
        return textContent.getTranslation().stream()
                .filter((t) -> t.getTranslationEmbeddedId().getLanguage().getName().equals(language.name()))
                .findFirst()
                .map(Translation::getText)
                .orElse(textContent.getOriginalText());
    }

    @Mapping(source = "name.originalLanguageName", target = "name.originalLanguage.name")
    @Mapping(source = "name.originalLanguageId", target = "name.originalLanguage.id")
    Color toEntity(ColorFullDto colorFullDto);


    ColorFullDto toAdminDto(Color color);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Color partialUpdate(ColorFullDto colorFullDto, @MappingTarget Color color);
}
