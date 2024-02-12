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

import ua.flowerista.shop.dto.ColorDto;
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
		ColorDto dto = new ColorDto();
		Mockito.when(mapper.toEntity(any(ColorDto.class))).thenReturn(new Color());
		service.insert(dto);
		verify(repository, times(1)).save(any(Color.class));
		verify(mapper, times(1)).toEntity(any(ColorDto.class));
	}

	@Test
	void testDeleteById() {
		service.deleteById(anyInt());
		verify(repository, times(1)).deleteById(anyInt());
	}

	@Test
	void testGetAllColors() {
		service.getAllColors();
		verify(repository, times(1)).findAll();
	}

	@Test
	void testGetColorById() {
		service.getColorById(anyInt());
		verify(repository, times(1)).getReferenceById(anyInt());
	}

	@Test
	void testUpdate() {
		ColorDto dto = new ColorDto();
		Mockito.when(mapper.toEntity(any(ColorDto.class))).thenReturn(new Color());
		service.update(dto);
		verify(repository, times(1)).save(any(Color.class));
		verify(mapper, times(1)).toEntity(any(ColorDto.class));
	}

}
