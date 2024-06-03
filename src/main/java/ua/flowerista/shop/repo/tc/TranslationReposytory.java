package ua.flowerista.shop.repo.tc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.tc.Translation;
import ua.flowerista.shop.models.tc.TranslationEmbeddedId;

@Repository
public interface TranslationReposytory extends JpaRepository<Translation, TranslationEmbeddedId> {
}
