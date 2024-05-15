package ua.flowerista.shop.repo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.flowerista.shop.models.Color;

import java.util.List;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
    @Override
    @Cacheable("colors")
    List<Color> findAll();
}
