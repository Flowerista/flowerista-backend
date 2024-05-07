package ua.flowerista.shop.services;

import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.mappers.ColorMapper;
import ua.flowerista.shop.models.Bouquete;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.models.Languages;
import ua.flowerista.shop.models.Translate;
import ua.flowerista.shop.repo.ColorRepository;

@Service
public class ColorService {

	private ColorRepository repo;
	private ColorMapper mapper;

	@Autowired
	public ColorService(ColorRepository repo, ColorMapper mapper) {
		this.repo = repo;
		this.mapper = mapper;
	}

	public void insert(ColorDto colorDto) {
		repo.save(mapper.toEntity(colorDto));
	}

	public void deleteById(int id) {
		repo.deleteById(id);
	}

	public List<ColorDto> getAllColors() {
		trans();
		return repo.findAll().stream().map(color -> mapper.toDto(color)).collect(Collectors.toList());
	}
	public List<ColorDto> getAllColors(Languages lang) {
		return repo.findAll().stream().map(color -> mapper.toDto(color, lang)).collect(Collectors.toList());
	}

	public ColorDto getColorById(int id) {
		return mapper.toDto(repo.getReferenceById(id));
	}

	public void update(ColorDto color) {
		repo.save(mapper.toEntity(color));
	}

    public Boolean isColorExist(Integer colorId) {
		return repo.existsById(colorId);
    }
}
