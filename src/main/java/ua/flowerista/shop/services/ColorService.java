package ua.flowerista.shop.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.mappers.ColorMapper;
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
		return repo.findAll().stream().map(color -> mapper.toDto(color)).collect(Collectors.toList());
	}

	public ColorDto getColorById(int id) {
		return mapper.toDto(repo.getReferenceById(id));
	}

	public void update(ColorDto color) {
		repo.save(mapper.toEntity(color));
	}

}
