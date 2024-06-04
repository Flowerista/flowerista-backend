package ua.flowerista.shop.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.repositories.FlowerRepository;

@ExtendWith(MockitoExtension.class)
class FlowerServiceTest {

	@Mock
	private FlowerRepository repository;
	@Mock
	private FlowerMapper mapper;
	@InjectMocks
	FlowerService service;

	@Test
	void testInsert() {
		Flower flower = new Flower();
		service.insert(flower);
		verify(repository, times(1)).save(any(Flower.class));
	}

	@Test
	void testDeleteById() {
		service.deleteById(anyInt());
		verify(repository, times(1)).deleteById(anyInt());
	}

	@Test
	void testGetAllFlowers() {
		service.getAll();
		verify(repository, times(1)).findAll();
	}

	@Test
	void testGetFlowerById() {
		service.getById(anyInt());
		verify(repository, times(1)).findById(anyInt());
	}

	@Test
	void testUpdate() {
		Flower dto = new Flower();
		service.update(dto);
		verify(repository, times(1)).save(any(Flower.class));
	}

}
