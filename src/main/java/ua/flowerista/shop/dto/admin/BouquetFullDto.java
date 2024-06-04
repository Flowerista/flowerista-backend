package ua.flowerista.shop.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DTO for {@link ua.flowerista.shop.models.Bouquet}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BouquetFullDto implements Serializable {
    Integer id;
    Set<FlowerFullDto> flowers;
    Set<ColorFullDto> colors;
    String itemCode;
    Map<Integer, String> imageUrls;
    List<BouquetSizeFullDto> sizes;
    int availableQuantity;
    int soldQuantity;
    TextContentDto name;
}
