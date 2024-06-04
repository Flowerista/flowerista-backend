package ua.flowerista.shop.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.transaction.annotation.Transactional;
import ua.flowerista.shop.models.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {

//	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
	boolean existsByEmail(@Param("email") String email);

//	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phoneNumber = :phoneNumber")
	boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

	Optional<User> findByEmail(String email);

	@Transactional
	@Modifying
	@Query("update User u set u.enabled = true where u.email = ?1")
	void updateEnabledByEmail(String email);

	@Transactional
	@Modifying
	@Query("update User u set u.password = ?1 where u.email = ?2")
	void updatePasswordByEmail(String password, String email);
}
