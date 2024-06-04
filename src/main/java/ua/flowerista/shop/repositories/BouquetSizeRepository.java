package ua.flowerista.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.BouquetSize;

import java.math.BigInteger;

@Repository
public interface BouquetSizeRepository extends JpaRepository<BouquetSize, Integer> {
    @Query("SELECT CASE " +
            "WHEN bs.isSale = true THEN bs.discountPrice " +
            "ELSE bs.defaultPrice " +
            "END " +
            "FROM BouquetSize bs WHERE bs.id = :sizeId")
    BigInteger getPriceById(Integer sizeId);
}
