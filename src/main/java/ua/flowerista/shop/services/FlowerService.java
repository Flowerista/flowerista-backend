package ua.flowerista.shop.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.mappers.FlowerMapper;
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

	public FlowerDto getFlowerById(int id) {
		return mapper.toDto(repo.getReferenceById(id));
	}

	public void update(FlowerDto flower) {
		repo.save(mapper.toEntity(flower));
	}

}
