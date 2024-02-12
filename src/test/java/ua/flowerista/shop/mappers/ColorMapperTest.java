package ua.flowerista.shop.mappers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.models.Color;

class ColorMapperTest {
	
	private final ColorMapper mapper = new ColorMapper();
	
	@Test
	void testToEntity() {
		ColorDto dto = new ColorDto();
		dto.setId(1);
		dto.setName("1");
		
		Color entity = mapper.toEntity(dto);
		
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getName(), entity.getName());
		
	}

    @Test
    void testToDto() {
        Color entity = new Color();
        entity.setId(1);
        entity.setName("Red");

        ColorDto dto = mapper.toDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
    }

}
