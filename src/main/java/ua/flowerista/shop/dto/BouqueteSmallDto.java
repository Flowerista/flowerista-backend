package ua.flowerista.shop.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.flowerista.shop.models.BouqueteSize;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BouqueteSmallDto {
	private int id;
	private String name;
    private Map<Integer, String> imageUrls;
	private BigDecimal defaultPrice;
	private BigDecimal discount;
	private BigDecimal discountPrice;
	private Set<BouqueteSize> sizes;
	private int stockQuantity;
}
