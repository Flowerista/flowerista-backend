package ua.flowerista.shop.repositories;

import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.QFlower;

@Repository
public interface FlowerRepository extends JpaRepository<Flower, Integer>, QuerydslPredicateExecutor<Flower>, QuerydslBinderCustomizer<QFlower> {
    @Override
    default void customize(QuerydslBindings bindings, QFlower root) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) (path, s) -> path.equalsIgnoreCase(s));
    }

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Flower f WHERE f.name.originalText = ?1")
    boolean existsByName(String name);
}
