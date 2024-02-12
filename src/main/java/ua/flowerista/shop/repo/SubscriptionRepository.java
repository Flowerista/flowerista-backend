package ua.flowerista.shop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ua.flowerista.shop.models.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
	
	@Query("SELECT COUNT(s) > 0 FROM Subscription s WHERE s.email = :email")
	boolean existsByEmail(@Param("email") String email);

}
