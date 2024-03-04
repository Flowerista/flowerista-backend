package ua.flowerista.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.flowerista.shop.models.BouqueteSize;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BouqueteSmallDto {
    private int id;
    private String name;
    private Map<Integer, String> imageUrls;
    private BigInteger defaultPrice;
    private BigInteger discount;
    private BigInteger discountPrice;
    private Set<BouqueteSize> sizes;
    private int stockQuantity;
}
