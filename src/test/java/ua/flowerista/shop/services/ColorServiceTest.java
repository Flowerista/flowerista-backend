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

import ua.flowerista.shop.mappers.ColorMapper;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.repo.ColorRepository;

@ExtendWith(MockitoExtension.class)
class ColorServiceTest {

	@Mock
	private ColorRepository repository;
	@Mock
	private ColorMapper mapper;
	@InjectMocks
	ColorService service;

	@Test
	void testInsert() {
		Color color = new Color();
		service.insert(color);
		verify(repository, times(1)).save(any(Color.class));
	}

	@Test
	void testDeleteById() {
		service.deleteById(anyInt());
		verify(repository, times(1)).deleteById(anyInt());
	}

	@Test
	void testGetAllColors() {
		service.getAll();
		verify(repository, times(1)).findAll();
	}

	@Test
	void testGetColorById() {
		service.getById(anyInt());
		verify(repository, times(1)).getReferenceById(anyInt());
	}

	@Test
	void testUpdate() {
		Color color = new Color();
		service.update(color);
		verify(repository, times(1)).save(any(Color.class));
	}

}
