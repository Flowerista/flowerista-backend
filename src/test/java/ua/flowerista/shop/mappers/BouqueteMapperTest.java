package ua.flowerista.shop.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.flowerista.shop.dto.BouqueteDto;
import ua.flowerista.shop.models.*;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        //given
        BouqueteDto dto = new BouqueteDto();
        //when
        Bouquete entity = mapper.toEntity(dto);
        //then
        assertNull(entity, "Entity should be null until implemented");
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
        size1.setDefaultPrice(BigInteger.valueOf(123));

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
