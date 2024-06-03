package ua.flowerista.shop.repo.tc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.tc.Color;

@Repository
public interface CR extends JpaRepository<Color, Integer> {
    @Query("SELECT c FROM Color c WHERE c.name.originalText = :text")
    Color findByName(String text);
}
