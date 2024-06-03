package ua.flowerista.shop.repo.tc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.tc.Language;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Language findByName(String en);
}
