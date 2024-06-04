package ua.flowerista.shop.repositories.textContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.textContent.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
    Language findByName(String name);
}
