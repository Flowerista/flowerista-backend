package ua.flowerista.shop.repo.tc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.tc.Bouquet;

@Repository
public interface BTR extends JpaRepository<Bouquet, Integer> {
}
