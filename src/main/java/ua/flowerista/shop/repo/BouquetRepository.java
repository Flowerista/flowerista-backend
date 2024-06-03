package ua.flowerista.shop.repo;

import java.util.List;

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

@Repository
public interface BouquetRepository extends JpaRepository<Bouquet, Integer>, QuerydslPredicateExecutor<Bouquet>, QuerydslBinderCustomizer<QBouquet> {

	@Override
	default void customize(QuerydslBindings bindings, QBouquet root) {
		bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) (path, s) -> path.containsIgnoreCase(s));
	}
//	@Override
//	@Cacheable("bouquets")
//	@Query("SELECT b FROM Bouquet b JOIN FETCH b.flowers JOIN FETCH b.colors JOIN FETCH b.sizes JOIN FETCH b.translates")
//	List<Bouquet> findAll();
	@Cacheable("bouquetsTop5BySoldQuantity")
	List<Bouquet> findTop5ByOrderBySoldQuantityDesc();
	@Cacheable("bouquetsTop5Discount")
	@Query("SELECT b FROM Bouquet b JOIN b.sizes bs WHERE bs.discount IS NOT NULL AND bs.size = 'MEDIUM' ORDER BY bs.discount DESC LIMIT 5")
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

	@Query("SELECT MIN(COALESCE(bs.discountPrice, bs.defaultPrice)) FROM Bouquet b JOIN b.sizes bs WHERE bs.size = 'MEDIUM'")
	Integer findMinPrice();

	@Query("SELECT MAX(COALESCE(bs.discountPrice, bs.defaultPrice)) FROM Bouquet b JOIN b.sizes bs WHERE bs.size = 'MEDIUM'")
	Integer findMaxPrice();

	@Query("SELECT b FROM Bouquet b LEFT JOIN FETCH b.sizes WHERE b.id = :id")
	Bouquet findById(@Param(value = "id") int id);

	@Query("SELECT COUNT(b) > 0 FROM Bouquet b WHERE b.id = :productId and b.quantity > 0")
	Boolean isBouquetAvailableForSale(Integer productId);

}
