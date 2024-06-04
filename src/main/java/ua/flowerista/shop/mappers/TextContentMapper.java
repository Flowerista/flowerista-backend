package ua.flowerista.shop.mappers;

import org.mapstruct.*;
import ua.flowerista.shop.dto.admin.TextContentDto;
import ua.flowerista.shop.models.textContent.TextContent;
import ua.flowerista.shop.services.LanguageService;

@Mapper(componentModel = "spring", uses = {LanguageService.class, TranslationMapper.class})
public interface TextContentMapper {
    @Mapping(source = "originalLanguage.id", target = "originalLanguageId")
    @Mapping(source = "originalLanguage.name", target = "originalLanguageName")
    TextContentDto toDto(TextContent textContent);

    @Mapping(source = "originalLanguageId", target = "originalLanguage")
    TextContent toEntity(TextContentDto textContentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TextContent partialUpdate(TextContentDto textContentDto, @MappingTarget TextContent textContent);
}
