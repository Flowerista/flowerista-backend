package ua.flowerista.shop.services;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.models.Translate;
import ua.flowerista.shop.repo.FlowerRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FlowerService {
    private static final Logger logger = LoggerFactory.getLogger(FlowerService.class);
    private final FlowerRepository flowerRepository;

    public void insert(Flower flower) {
        flowerRepository.save(flower);
    }

    public void deleteById(Integer id) {
        flowerRepository.deleteById(id);
    }

    public List<Flower> getAll() {
        return flowerRepository.findAll();
    }

    public Optional<Flower> getById(Integer id) {
        return flowerRepository.findById(id);
    }

    public void update(Flower flower) {
        flowerRepository.save(flower);
    }

    public void update(FlowerDto flower, Languages lang) {
        Flower entity = getById(flower.getId())
                .orElseThrow(() -> {
                    logger.error("Flower not found {} ", flower.getId());
                    return new AppException("Flower not found", HttpStatus.INTERNAL_SERVER_ERROR);
                });

        if (lang == Languages.en) {
            entity.setName(flower.getName());
        }

//        Set<Translate> translates = entity != null ? entity.getNameTranslate() : null;
//
//        Translate translate;
//        if (translates != null) {
//            translate = translates.stream().filter(t -> t.getLanguage() == lang).findFirst().orElse(new Translate());
//            translates.remove(translate);
//        } else {
//            translate = new Translate();
//        }
//        translate.setText(flower.getName());
//        translate.setLanguage(lang);
//        translate.setFlower(entity);
//        translates.add(translate);
//        entity.setNameTranslate(translates);
//        flowerRepository.save(entity);
    }

    public Page<Flower> getAll(Predicate predicate, Pageable pageable) {
        return flowerRepository.findAll(predicate, pageable);
    }

    public boolean isNameExist(String name) {
        return flowerRepository.existsByName(name);
    }
}
