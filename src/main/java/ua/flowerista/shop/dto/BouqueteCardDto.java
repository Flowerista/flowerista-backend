package ua.flowerista.shop.dto;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.flowerista.shop.models.BouqueteSize;
import ua.flowerista.shop.models.Flower;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BouqueteCardDto {

	private int id;
	private String name;
	private String itemCode;
    private Map<Integer, String> imageUrls;
    private Set<BouqueteSize> sizes;
    private Set<FlowerDto> flowers;
    private int stockQuantity;

}
