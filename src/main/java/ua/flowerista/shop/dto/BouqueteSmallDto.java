package ua.flowerista.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.flowerista.shop.models.Bouquet;
import ua.flowerista.shop.models.BouquetSize;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

/**
 * DTO for {@link Bouquet}
 */
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
    private Set<BouquetSize> sizes;
    private int stockQuantity;
}
