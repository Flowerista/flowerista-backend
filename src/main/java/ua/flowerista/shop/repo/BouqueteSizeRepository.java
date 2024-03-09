package ua.flowerista.shop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ua.flowerista.shop.models.BouqueteSize;

import java.math.BigInteger;

@Repository
public interface BouqueteSizeRepository extends JpaRepository<BouqueteSize, Integer> {
    @Query("SELECT CASE " +
            "WHEN bs.isSale = true THEN bs.discountPrice " +
            "ELSE bs.defaultPrice " +
            "END " +
            "FROM BouqueteSize bs WHERE bs.id = :sizeId")
    BigInteger getPriceById(Integer sizeId);
}
