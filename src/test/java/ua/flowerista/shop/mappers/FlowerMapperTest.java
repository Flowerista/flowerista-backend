package ua.flowerista.shop.mappers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.models.Flower;

class FlowerMapperTest {

	private final FlowerMapper mapper = new FlowerMapper();

	@Test
	void testToEntity() {
		FlowerDto dto = new FlowerDto();
		dto.setId(1);
		dto.setName("Red");

		Flower entity = mapper.toEntity(dto);

		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getName(), entity.getName());
	}

	@Test
	void testToDto() {
		Flower entity = new Flower();
		entity.setId(1);
		entity.setName("Red");

		FlowerDto dto = mapper.toDto(entity);

		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getName(), dto.getName());
	}

}
