package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.textContent.Language;
import ua.flowerista.shop.repositories.textContent.LanguageRepository;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;

    public Language getById(Integer id) {
        return languageRepository.findById(id).orElse(null);
    }

    public Language findByName(String name) {
        return languageRepository.findByName(name);
    }
}
