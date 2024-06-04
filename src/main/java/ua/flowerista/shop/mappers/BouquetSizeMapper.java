package ua.flowerista.shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ua.flowerista.shop.dto.BouquetSizeDto;
import ua.flowerista.shop.models.BouquetSize;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface BouquetSizeMapper {
    @Mapping(source = ".", target = "discount", qualifiedByName = "calculateDiscount")
    BouquetSizeDto toDto(BouquetSize bouquetSize);

    @Named("calculateDiscount")
    default BigInteger getDiscount(BouquetSize bouquetSize) {
        if (bouquetSize.getIsSale()) {
            return bouquetSize.getDefaultPrice()
                    .subtract(bouquetSize.getDiscountPrice())
                    .multiply(BigInteger.valueOf(100L))
                    .divide(bouquetSize.getDefaultPrice());
        } else {
            return BigInteger.ZERO;
        }
    }
}
