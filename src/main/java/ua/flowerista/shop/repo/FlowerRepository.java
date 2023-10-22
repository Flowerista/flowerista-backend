package ua.flowerista.shop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.flowerista.shop.models.Flower;

@Repository
public interface FlowerRepository extends JpaRepository<Flower, Integer>  {

}
