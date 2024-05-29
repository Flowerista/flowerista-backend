package ua.flowerista.shop.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.transaction.annotation.Transactional;
import ua.flowerista.shop.models.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {

	@Query(value = """
		      select t from Token t inner join User u\s
		      on t.user.id = u.id\s
		      where u.id = :id and (t.expired = false or t.revoked = false)\s
		      """)
		  List<Token> findAllValidTokenByUser(Integer id);

		  Optional<Token> findByToken(String token);

	void deleteByToken(String jwtToken);

	@Transactional
	@Modifying
	@Query("update Token t set t.revoked = true, t.expired = true where t.token in ?1")
	int updateRevokedAndExpiredByTokenIn(List<String> tokens);
}
