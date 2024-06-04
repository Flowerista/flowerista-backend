package ua.flowerista.shop.dto.admin;

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
public class BouquetSizeFullDto implements Serializable {
    Integer id;
    Size size;
    Integer bouquetId;
    @NotNull
    BigInteger defaultPrice;
    Boolean isSale;
    BigInteger discountPrice;
}
