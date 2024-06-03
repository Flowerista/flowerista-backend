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
import ua.flowerista.shop.models.*;
import ua.flowerista.shop.models.tc.*;
import ua.flowerista.shop.models.tc.Bouquet;
import ua.flowerista.shop.models.tc.BouquetSize;
import ua.flowerista.shop.models.tc.Color;
import ua.flowerista.shop.repo.*;
import ua.flowerista.shop.repo.tc.*;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(classes = {BouquetService.class, FlowerService.class, FlowerRepository.class, ColorRepository.class,
        TranslateRepository.class, TranslationReposytory.class, LanguageRepository.class, TextContentRepository.class,
        BTR.class,
        ColorService.class, BouquetRepository.class, CloudinaryService.class, PostgresTestProfileJPAConfig.class})
@ActiveProfiles("test")
class BouquetServiceTest {
    @MockBean
    private FlowerService flowerService;
    @MockBean
    private ColorService colorService;
    @Autowired
    private BouquetRepository bouquetRepository;
    @Autowired
    private FlowerRepository flowerRepository;
    @Autowired
    private BouquetSizeRepository bouquetSizeRepository;
    @Autowired
    private ColorRepository colorRepository;
    @MockBean
    private CloudinaryService cloudinary;
    @Autowired
    private BouquetService bouquetService;
    @Autowired
    private CR cr;
    @Autowired
    private TranslationReposytory translationReposytory;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private TextContentRepository textContentRepository;
    @Autowired
    private BTR btr;
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
//        bouquetRepository.deleteAll();

//        insertTestBouquets();
    }

    private void worksBouq(){
        Language en = new Language();
        en.setName("en");
        en = languageRepository.save(en);

        Language uk = new Language();
        uk.setName("uk");
        uk = languageRepository.save(uk);

        Bouquet bouquet = new Bouquet();
        TextContent textContent = new TextContent();

        Translation translation = new Translation();
        TranslationEmbeddedId translationEmbeddedId = new TranslationEmbeddedId();
        translationEmbeddedId.setLanguage(languageRepository.findByName("en"));
        translationEmbeddedId.setTextContent(textContent);
        translation.setTranslationEmbeddedId(translationEmbeddedId);
        translation.setText("test");

        Translation translation2 = new Translation();
        TranslationEmbeddedId translationEmbeddedId2 = new TranslationEmbeddedId();
        translationEmbeddedId2.setLanguage(languageRepository.findByName("uk"));
        translationEmbeddedId2.setTextContent(textContent);
        translation2.setTranslationEmbeddedId(translationEmbeddedId2);
        translation2.setText("тест");

        textContent.setTranslation(Set.of(translation, translation2));

        bouquet.setName(textContent);

        BouquetSize size = new BouquetSize();
        size.setSize(Size.LARGE);
        size.setDefaultPrice(BigInteger.valueOf(100));
        size.setIsSale(true);
        size.setDiscountPrice(BigInteger.valueOf(90));
        size.setBouquet(bouquet);
        bouquet.setSizes(Set.of(size));

        bouquet.setAvailableQuantity(10);
        bouquet.setSoldQuantity(0);
        Integer id = works();
        bouquet.setColors(Set.of(cr.findById(id).get()));

        btr.save(bouquet);

        btr.findAll();
    }
    private Integer works(){
//        Language en = new Language();
//        en.setName("en");
//        en = languageRepository.save(en);
//
//        Language uk = new Language();
//        uk.setName("uk");
//        uk = languageRepository.save(uk);

        Color color = new Color();
        TextContent textContent = new TextContent();

        Translation translation = new Translation();
        TranslationEmbeddedId translationEmbeddedId = new TranslationEmbeddedId();
        translationEmbeddedId.setLanguage(languageRepository.findByName("en"));
        translationEmbeddedId.setTextContent(textContent);
        translation.setTranslationEmbeddedId(translationEmbeddedId);
        translation.setText("test");

        Translation translation2 = new Translation();
        TranslationEmbeddedId translationEmbeddedId2 = new TranslationEmbeddedId();
        translationEmbeddedId2.setLanguage(languageRepository.findByName("uk"));
        translationEmbeddedId2.setTextContent(textContent);
        translation2.setTranslationEmbeddedId(translationEmbeddedId2);
        translation2.setText("тест");

        textContent.setTranslation(Set.of(translation, translation2));

        color.setName(textContent);
        Color saved = cr.save(color);
        return saved.getId();
//        cr.findAll();
    }
    private void insertTestBouquets() {
//        for (int i = 1; i <= 6; i++) {
//            Flower flower = new Flower();
//            flower.setName("flower" + i);
//            Translate translate = new Translate();
//            translate.setLanguage(Languages.en);
//            translate.setText("flower" + i);
//            flower.setNameTranslate(Set.of(translate));
//        }

        for (int i = 1; i <= 6; i++) {
            //set general info
            ua.flowerista.shop.models.Bouquet bouquet = new ua.flowerista.shop.models.Bouquet();
            bouquet.setName("bouquet" + i);
            bouquet.setQuantity(10);
            bouquet.setSoldQuantity(0);
            bouquet.setItemCode("itemCode" + i);
            //set translates
            {
                Translate translate = new Translate();
                translate.setLanguage(Languages.en);
                translate.setTitle("name");
                translate.setText("bouquet" + i);
                bouquet.setTranslates(Set.of(translate));
            }
            //set flowers
            {
                Flower flower = new Flower();
                flower.setName("flower" + i);
                Translate translate = new Translate();
                translate.setTitle("name");
                translate.setLanguage(Languages.en);
                translate.setText("flower" + i);
                flower.setNameTranslate(Set.of(translate));
                bouquet.setFlowers(Set.of(flower));
            }
            //set colors
            {
                ua.flowerista.shop.models.Color color = new ua.flowerista.shop.models.Color();
                color.setName("color" + i);
                Translate translate = new Translate();
                translate.setTitle("name");
                translate.setLanguage(Languages.en);
                translate.setText("color" + i);
                color.setNameTranslate(Set.of(translate));
                bouquet.setColors(Set.of(color));
            }
            //set sizes
            {
                Set<ua.flowerista.shop.models.BouquetSize> sizeSet = new HashSet<>();
                for (int j = 0; j < 3; j++) {
                    ua.flowerista.shop.models.BouquetSize size = new ua.flowerista.shop.models.BouquetSize();
                    size.setSize(Size.values()[j]);
                    // 110, 120, 130, 210, 220, 230 ... 610, 620, 630
                    size.setDefaultPrice(BigInteger.TEN
                            .multiply(BigInteger.valueOf(j + 1))
                            .add(BigInteger.valueOf(i*100)));
                    sizeSet.add(size);
                    size.setBouquet(bouquet);
                }
                bouquet.setSizes(sizeSet);
            }
            bouquetRepository.save(bouquet);
        }
    }


    @Test
    @DisplayName("Should return correct bouquet by id")
    void getByIdShouldReturnCorrectBouquet() {
        //given
        bouquetService.searchBouquets("bouquet1");
        Flower flower = new Flower();
        Translate translate = new Translate();
        flower.setName("flower0");
        translate.setLanguage(Languages.en);
        translate.setText("flower" + 0);
        flower.setNameTranslate(Set.of(translate));
        flowerRepository.save(flower);

        ua.flowerista.shop.models.Bouquet bouquet = new ua.flowerista.shop.models.Bouquet();
        bouquet.setName("test");
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
        bouquet.setName("test");
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
//
//	@Test
//	void testDeleteById() {
//		service.deleteById(anyInt());
//		verify(repository, times(1)).deleteById(anyInt());
//	}
//
//	@Test
//	void testGetAllBouquetes() {
//		service.getAllBouquetes();
//		verify(repository, times(1)).findAll();
//	}
//
//	@Test
//	void testGetBouqueteById() {
//		service.getBouqueteById(anyInt(), Languages.en);
//		verify(repository, times(1)).getReferenceById(anyInt());
//	}
//
//	@Test
//	void testUpdate() {
//		BouqueteDto dto = new BouqueteDto();
//		Mockito.when(mapper.toEntity(any(BouqueteDto.class))).thenReturn(new Bouquete());
//		service.update(dto);
//		verify(repository, times(1)).save(any(Bouquete.class));
//		verify(mapper, times(1)).toEntity(any(BouqueteDto.class));
//	}
//
//	@Test
//	void testGetBouquetesBestSellers() {
//		service.getBouquetsBestSellers();
//		verify(repository, times(1)).findTop5ByOrderBySoldQuantityDesc();
//	}
//
//	@Test
//	void testGetBouquetesTop5Sales() {
//		service.getBouquetsTop5Sales();
//		verify(repository, times(1)).findTop5ByOrderByDiscountDesc();
//	}
//
//	@Test
//	void testGetMinMaxPrices() {
//		service.getMinMaxPrices();
//		verify(repository, times(1)).findMaxPrice();
//		verify(repository, times(1)).findMinPrice();
//	}

}
