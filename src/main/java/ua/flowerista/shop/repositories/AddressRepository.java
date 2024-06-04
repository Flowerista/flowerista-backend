package ua.flowerista.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.flowerista.shop.models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

}
