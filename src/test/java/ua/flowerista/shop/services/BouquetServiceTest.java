package ua.flowerista.shop.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.flowerista.shop.config.PostgresTestProfileJPAConfig;
import ua.flowerista.shop.repositories.BouquetRepository;
import ua.flowerista.shop.repositories.textContent.LanguageRepository;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(classes = {BouquetService.class, FlowerService.class, LanguageRepository.class,
        ColorService.class, BouquetRepository.class, CloudinaryService.class, PostgresTestProfileJPAConfig.class})
@ActiveProfiles("test")
class BouquetServiceTest {
    @MockBean
    private FlowerService flowerService;
    @MockBean
    private ColorService colorService;
    @Autowired
    private BouquetRepository bouquetRepository;
    @MockBean
    private CloudinaryService cloudinary;
    @Autowired
    private BouquetService bouquetService;
    @Autowired
    private LanguageRepository languageRepository;

    @Container
    @ServiceConnection(name = "postgres")
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11-alpine")
            .withDatabaseName("integration-tests-db").withPassword("inmemory").withUsername("inmemory");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeAll
    public static void startContainer() {
        postgreSQLContainer.start();
    }

    @AfterAll
    public static void stopContainer() {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    public void setUp() {
        bouquetRepository.deleteAll();
//        insertTestBouquets();
    }


    @Test
    @DisplayName("Should return correct bouquet by id")
    void getByIdShouldReturnCorrectBouquet() {
        //given
        bouquetService.search("bouquet1");


        ua.flowerista.shop.models.Bouquet bouquet = new ua.flowerista.shop.models.Bouquet();

        bouquet.setSoldQuantity(0);
        bouquet.setFlowers(new HashSet<>());
        bouquet.setColors(new HashSet<>());
        bouquet.setItemCode("test");
        bouquetRepository.save(bouquet);
        //when
        Optional<ua.flowerista.shop.models.Bouquet> savedBouquet = bouquetRepository.findById(bouquet.getId());
        //then
        assertTrue(savedBouquet.isPresent(), "Bouquet should be present");
        assertNotNull(savedBouquet.get().getId(), "Bouquet id should be present and correct");
    }

    @Test
    @DisplayName("Should return correct bouquet by id")
    void getBouquetsBestSellersShouldBeCorrect() {
        //given
        ua.flowerista.shop.models.Bouquet bouquet = new ua.flowerista.shop.models.Bouquet();

        bouquet.setSoldQuantity(0);
        bouquet.setFlowers(new HashSet<>());
        bouquet.setColors(new HashSet<>());
        bouquet.setItemCode("test");
        bouquetRepository.save(bouquet);
        //when
        Optional<ua.flowerista.shop.models.Bouquet> savedBouquet = bouquetRepository.findById(bouquet.getId());
        //then
        assertTrue(savedBouquet.isPresent(), "Bouquet should be present");
        assertNotNull(savedBouquet.get().getId(), "Bouquet id should be present and correct");
    }

}
