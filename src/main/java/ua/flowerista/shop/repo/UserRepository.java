package ua.flowerista.shop.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ua.flowerista.shop.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
	boolean existsByEmail(@Param("email") String email);

	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phoneNumber = :phoneNumber")
	boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

	Optional<User> findByEmail(String email);

}
