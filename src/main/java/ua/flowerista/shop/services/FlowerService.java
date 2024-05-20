package ua.flowerista.shop.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.models.Translate;
import ua.flowerista.shop.repo.FlowerRepository;

@Service
public class FlowerService {

    private FlowerRepository repo;
    private FlowerMapper mapper;

    @Autowired
    public FlowerService(FlowerRepository repo, FlowerMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public void insert(FlowerDto flowerDto) {
        repo.save(mapper.toEntity(flowerDto));
    }

    public void deleteById(int id) {
        repo.deleteById(id);
    }

    public List<FlowerDto> getAllFlowers() {
        return repo.findAll().stream().map(flower -> mapper.toDto(flower)).collect(Collectors.toList());
    }

    public List<FlowerDto> getAllFlowers(Languages lang) {
        return repo.findAll().stream().map(flower -> mapper.toDto(flower, lang)).collect(Collectors.toList());
    }

    public FlowerDto getFlowerById(int id) {
        return mapper.toDto(repo.getReferenceById(id));
    }

    public void update(FlowerDto flower) {
        repo.save(mapper.toEntity(flower));
    }

    public void update(FlowerDto flower, Languages lang) {
        Flower entity = getFlower(flower.getId()).orElse(null); // достаем цветок для того, чтоб его изменить и сохранить

		if (lang == Languages.en) {
			entity.setName(flower.getName());
		}

        Set<Translate> translates = entity != null ? entity.getNameTranslate() : null; // достаем переводы цветка, для того, чтоб изх изменить

		Translate translate;
        if (translates != null) { // проверяем есть ли вообще у цветка переводы
            translate = translates.stream().filter(t -> t.getLanguage() == lang).findFirst().orElse(new Translate()); // достаем нужный нам перевод
			translates.remove(translate);
        } else {
			translate = new Translate();
		}
		translate.setText(flower.getName());
		translate.setLanguage(lang);
		translate.setFlower(entity);
		translates.add(translate);
		entity.setNameTranslate(translates);
		repo.save(entity);
    }

    public Page<Flower> getAllFlowers(Predicate predicate,
                                      Pageable pageable) {
        return repo.findAll(predicate, pageable);
    }

    public Optional<Flower> getFlower(Integer id) {
        return repo.findById(id);
    }

    public boolean isNameExist(String name) {
        return repo.existsByName(name);
    }
}
