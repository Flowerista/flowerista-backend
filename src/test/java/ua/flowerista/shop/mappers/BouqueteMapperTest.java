package ua.flowerista.shop.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.flowerista.shop.dto.BouqueteDto;
import ua.flowerista.shop.dto.ColorDto;
import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.models.Bouquete;
import ua.flowerista.shop.models.BouqueteSize;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.Size;

@ExtendWith(MockitoExtension.class)
class BouqueteMapperTest {

	@Mock
	private ColorMapper cMapper;
	@Mock
	private FlowerMapper fMapper;
	@InjectMocks
	private BouqueteMapper mapper;

	@Test
	void testToEntity() {
		BouqueteDto dto = new BouqueteDto();
		Set<FlowerDto> flowers = new HashSet<>();
		Set<ColorDto> colors = new HashSet<>();
		FlowerDto fDto1 = new FlowerDto();
		fDto1.setId(1);
		fDto1.setName("1");
		FlowerDto fDto2 = new FlowerDto();
		fDto2.setId(2);
		fDto2.setName("2");
		ColorDto cDto1 = new ColorDto();
		cDto1.setId(1);
		cDto1.setName("1");
		ColorDto cDto2 = new ColorDto();
		cDto2.setId(2);
		cDto2.setName("2");
		flowers.add(fDto1);
		flowers.add(fDto2);
		colors.add(cDto1);
		colors.add(cDto2);

		Set<BouqueteSize> sizes = new HashSet<>();
		BouqueteSize size1 = new BouqueteSize();
		size1.setId(1);
		size1.setSize(Size.MEDIUM);
		size1.setDefaultPrice(BigDecimal.valueOf(123));


		dto.setSizes(sizes);
        dto.setId(1);
        dto.setFlowers(flowers);
        dto.setColors(colors);
        dto.setItemCode("ABC123");
        dto.setName("Sample Bouquet");
        dto.setQuantity(50);
        dto.setSoldQuantity(20);

        Bouquete entity = mapper.toEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getSizes(), entity.getSizes());
        assertEquals(dto.getItemCode(), entity.getItemCode());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getQuantity(), entity.getQuantity());
        assertEquals(dto.getSoldQuantity(), entity.getSoldQuantity());
        verify(cMapper, times(2)).toEntity(any(ColorDto.class));
        verify(fMapper, times(2)).toEntity(any(FlowerDto.class));
	}

	@Test
	void testToDto() {
		Bouquete entity = new Bouquete();
		Set<Flower> flowers = new HashSet<>();
		Set<Color> colors = new HashSet<>();
		Flower flower1 = new Flower();
		flower1.setId(1);
		flower1.setName("1");
		Flower flower2 = new Flower();
		flower2.setId(2);
		flower2.setName("2");
		Color color1 = new Color();
		color1.setId(1);
		color1.setName("1");
		Color color2 = new Color();
		color2.setId(2);
		color2.setName("2");
		flowers.add(flower1);
		flowers.add(flower2);
		colors.add(color1);
		colors.add(color2);

		Set<BouqueteSize> sizes = new HashSet<>();
		BouqueteSize size1 = new BouqueteSize();
		size1.setId(1);
		size1.setSize(Size.MEDIUM);
		size1.setDefaultPrice(BigDecimal.valueOf(123));

        entity.setId(1);
        entity.setSizes(sizes);
        entity.setFlowers(flowers);
        entity.setColors(colors);
        entity.setItemCode("ABC123");
        entity.setName("Sample Bouquet");
        entity.setQuantity(50);
        entity.setSoldQuantity(20);

        BouqueteDto dto = mapper.toDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getSizes(), dto.getSizes());
        assertEquals(entity.getItemCode(), dto.getItemCode());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getQuantity(), dto.getQuantity());
        assertEquals(entity.getSoldQuantity(), dto.getSoldQuantity());
        verify(cMapper, times(2)).toDto(any(Color.class));
        verify(fMapper, times(2)).toDto(any(Flower.class));

	}

}
