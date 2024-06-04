package ua.flowerista.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.flowerista.shop.models.Size;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * DTO for {@link ua.flowerista.shop.models.BouquetSize}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BouquetSizeDto implements Serializable {
    private Integer id;
    private Size size;
    @NotNull
    private BigInteger defaultPrice;
    private Boolean isSale;
    private BigInteger discountPrice;
    private BigInteger discount;
}
