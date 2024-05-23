package ua.flowerista.shop.repo;

import java.util.List;

import com.querydsl.core.types.dsl.StringPath;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ua.flowerista.shop.models.Bouquete;
import ua.flowerista.shop.models.QBouquete;

@Repository
public interface BouqueteRepository extends JpaRepository<Bouquete, Integer>, QuerydslPredicateExecutor<Bouquete>, QuerydslBinderCustomizer<QBouquete> {

	@Override
	default void customize(QuerydslBindings bindings, QBouquete root) {
		bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) (path, s) -> path.containsIgnoreCase(s));
	}
	@Override
	@Cacheable("boquets")
	@Query("SELECT b FROM Bouquete b JOIN FETCH b.flowers JOIN FETCH b.colors JOIN FETCH b.sizes ")
	List<Bouquete> findAll();
	@Cacheable("boquetsTop5BySoldQuantity")
	List<Bouquete> findTop5ByOrderBySoldQuantityDesc();
	@Cacheable("boquetsTop5Discount")
	@Query("SELECT b FROM Bouquete b JOIN b.sizes bs WHERE bs.discount IS NOT NULL AND bs.size = 'MEDIUM' ORDER BY bs.discount DESC LIMIT 5")
	List<Bouquete> findTop5ByOrderByDiscountDesc();

	@Query("SELECT b.id FROM Bouquete b " + "WHERE "
			+ "(:flowerIds IS NULL OR EXISTS (SELECT 1 FROM b.flowers flower WHERE flower.id IN :flowerIds)) AND "
			+ "(:colorIds IS NULL OR EXISTS (SELECT 1 FROM b.colors color WHERE color.id IN :colorIds)) AND "
			+ "(:minPrice IS NULL OR EXISTS (SELECT 1 FROM b.sizes bs WHERE bs.size = 'MEDIUM' AND COALESCE(bs.discountPrice, bs.defaultPrice) >= :minPrice)) AND "
			+ "(:maxPrice IS NULL OR EXISTS (SELECT 1 FROM b.sizes bs WHERE bs.size = 'MEDIUM' AND COALESCE(bs.discountPrice, bs.defaultPrice) <= :maxPrice)) "
			+ "ORDER BY " + "CASE WHEN :sortByNewest = true THEN b.id END DESC, "
			+ "CASE WHEN :sortByPriceHighToLow = true THEN "
			+ "(SELECT COALESCE(bs2.discountPrice, bs2.defaultPrice) FROM BouqueteSize bs2 WHERE bs2.bouquete = b AND bs2.size = 'MEDIUM') END DESC, "
			+ "CASE WHEN :sortByPriceLowToHigh = true THEN "
			+ "(SELECT COALESCE(bs3.discountPrice, bs3.defaultPrice) FROM BouqueteSize bs3 WHERE bs3.bouquete = b AND bs3.size = 'MEDIUM') END ASC")
	List<Integer> findByFilters(@Param("flowerIds") List<Integer> flowerIds, @Param("colorIds") List<Integer> colorIds,
			@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice,
			@Param("sortByNewest") Boolean sortByNewest, @Param("sortByPriceHighToLow") Boolean sortByPriceHighToLow,
			@Param("sortByPriceLowToHigh") Boolean sortByPriceLowToHigh);

	@Query("SELECT MIN(COALESCE(bs.discountPrice, bs.defaultPrice)) FROM Bouquete b JOIN b.sizes bs WHERE bs.size = 'MEDIUM'")
	Integer findMinPrice();

	@Query("SELECT MAX(COALESCE(bs.discountPrice, bs.defaultPrice)) FROM Bouquete b JOIN b.sizes bs WHERE bs.size = 'MEDIUM'")
	Integer findMaxPrice();

	@Query("SELECT b FROM Bouquete b LEFT JOIN FETCH b.sizes WHERE b.id = :id")
	Bouquete findById(@Param(value = "id") int id);

	@Query("SELECT b FROM Bouquete b LEFT JOIN Translate t on b.id = t.bouquete.id WHERE lower(t.text) LIKE lower(concat('%', :name, '%'))")
	List<Bouquete> searchByName(@Param("name") String name);

	@Query("SELECT COUNT(b) > 0 FROM Bouquete b WHERE b.id = :productId and b.quantity > 0")
	Boolean isBouqueteAvailableForSale(Integer productId);

	Page<Bouquete> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Bouquete> findByTranslatesTextContainingIgnoreCase(String text, Pageable pageable);
}
