package ua.flowerista.shop.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ua.flowerista.shop.config.PostgresTestProfileJPAConfig;
import ua.flowerista.shop.models.Bouquete;

@SpringBootTest(classes = PostgresTestProfileJPAConfig.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class BouqueteRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private BouqueteRepository repository;

	private final static String SCRIPT_DB = "db.sql";

	@Container
	@ClassRule
	public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11-alpine")
			.withDatabaseName("integration-tests-db").withPassword("inmemory").withUsername("inmemory")
			.withInitScript(SCRIPT_DB);

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
	}

	@AfterAll
	public static void stopContainer() {
		postgreSQLContainer.stop();
	}

	@Test
	void testInsertBouquete() {
		Bouquete expected = new Bouquete();
		expected.setName("Spring Bouquet1");
		expected.setItemCode("BQ005");
		expected.setQuantity(20);
		expected.setSoldQuantity(5);

		repository.save(expected);

		Bouquete actual = repository.getReferenceById(repository.findAll().size());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getItemCode(), actual.getItemCode());
		assertEquals(expected.getQuantity(), actual.getQuantity());
		assertEquals(expected.getSoldQuantity(), actual.getSoldQuantity());
	}

	@Test
	void testDeleteBouqueteById() {
		List<Bouquete> bouquets = repository.findAll();
		repository.deleteById(bouquets.get(1).getId());

		int expected = 2;
		int actual = repository.findAll().size();
		assertEquals(expected, actual);
	}

	@Test
	void testGetAllBouquetes() {
		int expected = 3;
		int actual = repository.findAll().size();
		assertEquals(expected, actual);
	}

	@Test
	void testGetBouqueteById() {
		Bouquete expected = repository.findAll().get(1);
		Bouquete actual = repository.getReferenceById(repository.findAll().get(1).getId());
		assertEquals(expected, actual);
	}

	@Test
	void testUpdateBouquete() {
		Bouquete expected = repository.findAll().get(1);
		int bouqueteId = expected.getId();
		expected.setName("Updated Bouquet");
		repository.save(expected);

		Bouquete actual = repository.getReferenceById(bouqueteId);
		assertEquals(expected.getName(), actual.getName());
	}

	@Test
	void testFindTop5ByOrderBySoldQuantityDesc() {
		List<Bouquete> bouquetes = repository.findTop5ByOrderBySoldQuantityDesc();
		assertEquals(bouquetes.get(0).getSoldQuantity(), 8);
		assertEquals(bouquetes.get(1).getSoldQuantity(), 5);
		assertEquals(bouquetes.get(2).getSoldQuantity(), 2);
	}

	@Test
	void testFindTop5ByOrderByDiscountDesc() {
		List<Bouquete> bouquetes = repository.findTop5ByOrderByDiscountDesc();
		assertEquals(bouquetes.size(), 2);
	}

	@Test
	void findByFilters() {
		List<Integer> colorIds = new LinkedList<>();
		colorIds.add(1);
		Pageable pageable = PageRequest.of(0, 20);
		Page<Bouquete> content = repository.findByFilters(null, colorIds, null, null, false, true, false, pageable);
		List<Bouquete> bouquetes = content.getContent();
		assertEquals(bouquetes.size(), 2);
		assertEquals(bouquetes.get(0).getId(), 1);
	}

	@Test
	void findMinPrice() {
		Integer min = repository.findMinPrice();
		assertEquals(45, min);
	}

	@Test
	void findMaxPrice() {
		Integer max = repository.findMaxPrice();
		assertEquals(50, max);
	}

}
