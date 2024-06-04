package ua.flowerista.shop.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link ua.flowerista.shop.models.textContent.TextContent}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextContentDto implements Serializable {
    Long id;
    String originalText;
    Integer originalLanguageId;
    String originalLanguageName;
    List<TranslationDto> translation;

    /**
     * DTO for {@link ua.flowerista.shop.models.textContent.Translation}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TranslationDto implements Serializable {
        Integer translationEmbeddedIdTextContentId;
        Integer translationEmbeddedIdLanguageId;
        String translationEmbeddedIdLanguageName;
        String text;
    }
}
