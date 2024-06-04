package ua.flowerista.shop.repositories.textContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.textContent.TextContent;

@Repository
public interface TextContentRepository extends JpaRepository<TextContent, Integer> {
}
