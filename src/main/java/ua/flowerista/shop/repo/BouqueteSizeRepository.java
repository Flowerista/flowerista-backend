package ua.flowerista.shop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.flowerista.shop.models.BouqueteSize;

@Repository
public interface BouqueteSizeRepository extends JpaRepository<BouqueteSize, Integer> {

}
