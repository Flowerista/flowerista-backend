package ua.flowerista.shop.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.flowerista.shop.dto.BouqueteDto;
import ua.flowerista.shop.mappers.BouqueteMapper;
import ua.flowerista.shop.models.Bouquete;
import ua.flowerista.shop.repo.BouqueteRepository;

@ExtendWith(MockitoExtension.class)
class BouqueteServiceTest {

	@Mock
	private BouqueteRepository repository;
	@Mock
	private BouqueteMapper mapper;
	@InjectMocks
	BouqueteService service;

	@Test
	void testInsert() {
		BouqueteDto dto = new BouqueteDto();
		Mockito.when(mapper.toEntity(any(BouqueteDto.class))).thenReturn(new Bouquete());
		service.insert(dto, anyList());
		verify(repository, times(1)).save(any(Bouquete.class));
		verify(mapper, times(1)).toEntity(any(BouqueteDto.class));
	}

	@Test
	void testDeleteById() {
		service.deleteById(anyInt());
		verify(repository, times(1)).deleteById(anyInt());
	}

	@Test
	void testGetAllBouquetes() {
		service.getAllBouquetes();
		verify(repository, times(1)).findAll();
	}

	@Test
	void testGetBouqueteById() {
		service.getBouqueteById(anyInt());
		verify(repository, times(1)).getReferenceById(anyInt());
	}

	@Test
	void testUpdate() {
		BouqueteDto dto = new BouqueteDto();
		Mockito.when(mapper.toEntity(any(BouqueteDto.class))).thenReturn(new Bouquete());
		service.update(dto);
		verify(repository, times(1)).save(any(Bouquete.class));
		verify(mapper, times(1)).toEntity(any(BouqueteDto.class));
	}

	@Test
	void testGetBouquetesBestSellers() {
		service.getBouquetesBestSellers();
		verify(repository, times(1)).findTop5ByOrderBySoldQuantityDesc();
	}

	@Test
	void testGetBouquetesTop5Sales() {
		service.getBouquetesTop5Sales();
		verify(repository, times(1)).findTop5ByOrderByDiscountDesc();
	}
	
	@Test
	void testGetMinMaxPrices() {
		service.getMinMaxPrices();
		verify(repository, times(1)).findMaxPrice();
		verify(repository, times(1)).findMinPrice();
	}

}
