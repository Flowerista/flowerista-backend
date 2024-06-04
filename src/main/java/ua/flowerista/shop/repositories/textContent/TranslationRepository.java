package ua.flowerista.shop.repositories.textContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.textContent.Translation;
import ua.flowerista.shop.models.textContent.TranslationEmbeddedId;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, TranslationEmbeddedId> {
}
