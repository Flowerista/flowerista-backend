package ua.flowerista.shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.flowerista.shop.dto.admin.TextContentDto;
import ua.flowerista.shop.models.textContent.Translation;
import ua.flowerista.shop.repositories.textContent.TranslationRepository;
import ua.flowerista.shop.services.LanguageService;
import ua.flowerista.shop.services.TextContentService;

@Mapper(componentModel = "spring", uses = {TranslationRepository.class, TextContentService.class,
        LanguageService.class})
public interface TranslationMapper {

    @Mapping(source = "translationEmbeddedId.language.name", target = "translationEmbeddedIdLanguageName")
    @Mapping(source = "translationEmbeddedId.language.id", target = "translationEmbeddedIdLanguageId")
    @Mapping(source = "translationEmbeddedId.textContent.id", target = "translationEmbeddedIdTextContentId")
    TextContentDto.TranslationDto toDto(Translation translation);

    @Mapping(source = "translationEmbeddedIdTextContentId", target = "translationEmbeddedId.textContent")
    @Mapping(source = "translationEmbeddedIdLanguageId", target = "translationEmbeddedId.language")
    Translation toEntity(TextContentDto.TranslationDto translationDto);
}
