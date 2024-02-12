package ua.flowerista.shop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.flowerista.shop.models.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {

}
