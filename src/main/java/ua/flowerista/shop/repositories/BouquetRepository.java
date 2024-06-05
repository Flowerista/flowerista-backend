package ua.flowerista.shop.repositories;

import com.querydsl.core.types.dsl.StringPath;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.Bouquet;
import ua.flowerista.shop.models.QBouquet;

import java.util.List;

@Repository
public interface BouquetRepository extends JpaRepository<Bouquet, Integer>, QuerydslPredicateExecutor<Bouquet>, QuerydslBinderCustomizer<QBouquet> {

    @Override
    default void customize(QuerydslBindings bindings, QBouquet root) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) (path, s) -> path.containsIgnoreCase(s));
    }
    @Cacheable("bouquets")
    @Query("SELECT b FROM Bouquet b")
    List<Bouquet> findAllCached();

    @Cacheable("bouquetsTop5BySoldQuantity")
    @Query("SELECT b FROM Bouquet b ORDER BY b.soldQuantity DESC limit 5")
    List<Bouquet> findTop5ByOrderBySoldQuantityDesc();

    @Cacheable("bouquetsTop5Discount")
    @Query(value = "select * from bouquets b " +
            "where b.id in " +
            "(select bs.bouquet_id " +
            "from bouquets_sizes bs " +
            "where bs.is_sale = true " +
            "order by (bs.default_price - bs.discount_price))  " +
            "limit 5",
            nativeQuery = true)
    List<Bouquet> findTop5ByOrderByDiscountDesc();

    @Query("SELECT b.id FROM Bouquet b " + "WHERE "
            + "(:flowerIds IS NULL OR EXISTS (SELECT 1 FROM b.flowers flower WHERE flower.id IN :flowerIds)) AND "
            + "(:colorIds IS NULL OR EXISTS (SELECT 1 FROM b.colors color WHERE color.id IN :colorIds)) AND "
            + "(:minPrice IS NULL OR EXISTS (SELECT 1 FROM b.sizes bs WHERE bs.size = 'MEDIUM' AND COALESCE(bs.discountPrice, bs.defaultPrice) >= :minPrice)) AND "
            + "(:maxPrice IS NULL OR EXISTS (SELECT 1 FROM b.sizes bs WHERE bs.size = 'MEDIUM' AND COALESCE(bs.discountPrice, bs.defaultPrice) <= :maxPrice)) "
            + "ORDER BY " + "CASE WHEN :sortByNewest = true THEN b.id END DESC, "
            + "CASE WHEN :sortByPriceHighToLow = true THEN "
            + "(SELECT COALESCE(bs2.discountPrice, bs2.defaultPrice) FROM BouquetSize bs2 WHERE bs2.bouquet = b AND bs2.size = 'MEDIUM') END DESC, "
            + "CASE WHEN :sortByPriceLowToHigh = true THEN "
            + "(SELECT COALESCE(bs3.discountPrice, bs3.defaultPrice) FROM BouquetSize bs3 WHERE bs3.bouquet = b AND bs3.size = 'MEDIUM') END ASC")
    List<Integer> findByFilters(@Param("flowerIds") List<Integer> flowerIds, @Param("colorIds") List<Integer> colorIds,
                                @Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice,
                                @Param("sortByNewest") Boolean sortByNewest, @Param("sortByPriceHighToLow") Boolean sortByPriceHighToLow,
                                @Param("sortByPriceLowToHigh") Boolean sortByPriceLowToHigh);

    @Query("SELECT MIN(COALESCE(bs.discountPrice, bs.defaultPrice)) FROM BouquetSize bs")
    Integer findMinPrice();

    @Query("SELECT MAX(COALESCE(bs.discountPrice, bs.defaultPrice)) FROM BouquetSize bs")
    Integer findMaxPrice();

    @Query("SELECT b FROM Bouquet b LEFT JOIN FETCH b.sizes WHERE b.id = :id")
    Bouquet findById(@Param(value = "id") int id);

    @Query("SELECT COUNT(b) > 0 FROM Bouquet b WHERE b.id = :productId and b.availableQuantity > 0")
    Boolean isBouquetAvailableForSale(Integer productId);

}
