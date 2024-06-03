package ua.flowerista.shop.repo.tc;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.tc.Flower;

@Repository
public interface FR extends org.springframework.data.jpa.repository.JpaRepository<Flower, Integer>{

    @Query("SELECT f FROM Flower f WHERE f.name.originalText = :text")
    Flower findByName(String text);
}
