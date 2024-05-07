package ua.flowerista.shop.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Color;
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

	private void trans(){
		List<Flower> colors = repo.findAll();
		for (Flower flower : colors) {
			Translate translateEn = Translate.builder()
					.flower(flower)
					.language(Languages.EN)
					.text(flower.getName())
					.build();
			Translate translateUa = Translate.builder()
					.flower(flower)
					.language(Languages.UK)
					.text(translate(flower.getName()))
					.build();
			flower.getNameTranslate().add(translateEn);
			flower.getNameTranslate().add(translateUa);
			repo.save(flower);
		}
		repo.saveAll(colors);
	}

	public String translate(String str) {


		//Translate utility.translate = TranslateOptions.getDefaultInstance().getService();
		com.google.cloud.translate.Translate translate = TranslateOptions.newBuilder().setApiKey("AIzaSyADFI6uM11stLydgP9J0IweQx3WHJD_eo4").build().getService();
		Translation translation = translate.translate(
				str,
				com.google.cloud.translate.Translate.TranslateOption.sourceLanguage("en"),
				com.google.cloud.translate.Translate.TranslateOption.targetLanguage("uk"));
		return translation.getTranslatedText();
	}
}
