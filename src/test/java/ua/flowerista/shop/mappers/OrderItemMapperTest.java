package ua.flowerista.shop.mappers;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.flowerista.shop.dto.OrderItemDto;
import ua.flowerista.shop.models.*;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class OrderItemMapperTest {

    @InjectMocks
    OrderItemMapper orderItemMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void toDto() {
        //given
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        Bouquet bouquet = new Bouquet();
        bouquet.setId(1);
        orderItem.setBouquet(bouquet);
        orderItem.setName("Bouquete");
        orderItem.setQuantity(1);
        Color color = new Color();
        color.setId(1);
        BouquetSize size = new BouquetSize();
        size.setId(1);
        size.setSize(Size.SMALL);
        orderItem.setSize(size);
        orderItem.setPrice(BigInteger.valueOf(100));
        //when
        OrderItemDto dto = orderItemMapper.toDto(orderItem);
        //then
        assertEquals("Bouquete", dto.getName());
        assertEquals(1, dto.getQuantity());
        assertEquals(1, dto.getSizeId());
        assertEquals(100, dto.getPrice().intValue());
    }
}
