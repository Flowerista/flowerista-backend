package ua.flowerista.shop.repo.tc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.tc.BouquetSize;

@Repository
public interface BZTR extends JpaRepository<BouquetSize, Integer> {
}
