package ua.flowerista.shop.repo.tc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.Translate;
@Repository
public interface TranslateRepository extends JpaRepository<Translate, Long> {
}
