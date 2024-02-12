package ua.flowerista.shop.dto;

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
public class BouqueteDto {

	private int id;
	private Set<FlowerDto> flowers;
	private Set<ColorDto> colors;
	private String itemCode;
	private String name;
    private Map<Integer, String> imageUrls;
	private Set<BouqueteSize> sizes;
	private int quantity;
	private int soldQuantity;

}
