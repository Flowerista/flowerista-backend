package ua.flowerista.shop.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.mappers.FlowerMapper;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.repo.FlowerRepository;

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
		FlowerDto dto = new FlowerDto();
		Mockito.when(mapper.toEntity(any(FlowerDto.class))).thenReturn(new Flower());
		service.insert(dto);
		verify(repository, times(1)).save(any(Flower.class));
		verify(mapper, times(1)).toEntity(any(FlowerDto.class));
	}

	@Test
	void testDeleteById() {
		service.deleteById(anyInt());
		verify(repository, times(1)).deleteById(anyInt());
	}

	@Test
	void testGetAllFlowers() {
		service.getAllFlowers();
		verify(repository, times(1)).findAll();
	}

	@Test
	void testGetFlowerById() {
		service.getFlowerById(anyInt());
		verify(repository, times(1)).getReferenceById(anyInt());
	}

	@Test
	void testUpdate() {
		FlowerDto dto = new FlowerDto();
		Mockito.when(mapper.toEntity(any(FlowerDto.class))).thenReturn(new Flower());
		service.update(dto);
		verify(repository, times(1)).save(any(Flower.class));
		verify(mapper, times(1)).toEntity(any(FlowerDto.class));
	}

}
