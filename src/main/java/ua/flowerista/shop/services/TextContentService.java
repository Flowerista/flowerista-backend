package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.textContent.TextContent;
import ua.flowerista.shop.models.textContent.Translation;
import ua.flowerista.shop.models.textContent.TranslationEmbeddedId;
import ua.flowerista.shop.repositories.textContent.TextContentRepository;
import ua.flowerista.shop.repositories.textContent.TranslationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TextContentService {
    private final LanguageService languageService;
    private final TextContentRepository textContentRepository;
    private final TranslationRepository translationRepository;

    public TextContent getById(Integer id) {
        return textContentRepository.findById(id).orElse(null);
    }

    public TextContent getNewTextContent(String name) {
        TextContent textContent = new TextContent();
        textContent.setOriginalText(name);
        textContent.setOriginalLanguage(languageService.getById(1));
        List<Translation> translations = new ArrayList<>();

        Translation en = new Translation();
        TranslationEmbeddedId enEmbeddedId = new TranslationEmbeddedId();
        enEmbeddedId.setLanguage(languageService.findByName("en"));
        enEmbeddedId.setTextContent(textContent);
        en.setTranslationEmbeddedId(enEmbeddedId);
        en.setText("en ");
        translations.add(en);

        Translation uk = new Translation();
        TranslationEmbeddedId ukEmbeddedId = new TranslationEmbeddedId();
        ukEmbeddedId.setLanguage(languageService.findByName("uk"));
        ukEmbeddedId.setTextContent(textContent);
        uk.setTranslationEmbeddedId(ukEmbeddedId);
        uk.setText("uk ");
        translations.add(uk);

        textContent.setTranslation(translations);
        return textContentRepository.save(textContent);
    }
}
