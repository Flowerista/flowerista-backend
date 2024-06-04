package ua.flowerista.shop.repositories;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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
import ua.flowerista.shop.models.*;
import ua.flowerista.shop.models.textContent.Language;
import ua.flowerista.shop.models.textContent.TextContent;
import ua.flowerista.shop.models.textContent.Translation;
import ua.flowerista.shop.models.textContent.TranslationEmbeddedId;
import ua.flowerista.shop.repositories.textContent.LanguageRepository;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PostgresTestProfileJPAConfig.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class BouquetRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private BouquetRepository repository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private FlowerRepository flowerRepository;
    @Autowired
    private ColorRepository colorRepository;

    private final static String SCRIPT_DB = "db.sql";

    @Container
    @ClassRule
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

    public void init() {
        Language en = new Language();
        en.setName("en");
        en = languageRepository.save(en);

        Flower f = new Flower();
        TextContent textContent = new TextContent();
        textContent.setOriginalText("Rose");
        textContent.setOriginalLanguage(en);
        List<Translation> translations = new ArrayList<>();
        Translation translation = new Translation();
        TranslationEmbeddedId translationEmbeddedId = new TranslationEmbeddedId();
        translationEmbeddedId.setLanguage(en);
        translationEmbeddedId.setTextContent(textContent);
        translation.setTranslationEmbeddedId(translationEmbeddedId);
        translation.setText("Rose description");
        translations.add(translation);
        textContent.setTranslation(translations);
        f.setName(textContent);
        flowerRepository.save(f);

        Color c = new Color();
        TextContent colorName = new TextContent();
        colorName.setOriginalText("Red");
        colorName.setOriginalLanguage(en);
        List<Translation> colorTrans = new ArrayList<>();
        TranslationEmbeddedId translationEmbeddedIdColor = new TranslationEmbeddedId();
        translationEmbeddedIdColor.setLanguage(en);
        translationEmbeddedIdColor.setTextContent(colorName);
        Translation translationColor = new Translation();
        translationColor.setTranslationEmbeddedId(translationEmbeddedIdColor);
        translationColor.setText("Red description");
        colorTrans.add(translationColor);
        colorName.setTranslation(colorTrans);
        c.setName(colorName);
        colorRepository.save(c);
    }


    @Test
    void testInsertBouquet() {
        //given
        init();

        Bouquet b = new Bouquet();

        b.setItemCode("BQ-001");
        b.setAvailableQuantity(1);
        b.setSoldQuantity(0);
        Map<Integer, String> images = Map.of(1, "https://test.jpg");
        b.setImageUrls(images);

        TextContent textContent = new TextContent();
        textContent.setOriginalText("Bouquet 1");
        textContent.setOriginalLanguage(languageRepository.findByName("en"));

        List<Translation> translations = new ArrayList<>();

        TranslationEmbeddedId translationEmbeddedId = new TranslationEmbeddedId();
        Language language = languageRepository.findByName("en");
        translationEmbeddedId.setLanguage(language);
        translationEmbeddedId.setTextContent(textContent);

        Translation translation = new Translation();
        translation.setTranslationEmbeddedId(translationEmbeddedId);
        translation.setText("Bouquet 1 description");
        translations.add(translation);
        textContent.setTranslation(translations);
        b.setName(textContent);

        Set<Flower> flowers = flowerRepository.findAll().stream().collect(Collectors.toSet());
        b.setFlowers(flowers);

        Set<Color> colors = colorRepository.findAll().stream().collect(Collectors.toSet());
        b.setColors(colors);

        Set<BouquetSize> bouquetSizes = new HashSet<>();
        BouquetSize bz = new BouquetSize();
        bz.setDefaultPrice(BigInteger.valueOf(100));
        bz.setIsSale(true);
        bz.setSize(Size.SMALL);
        bz.setDiscountPrice(BigInteger.valueOf(80));
        bz.setBouquet(b);
        bouquetSizes.add(bz);
        b.setSizes(bouquetSizes);
        //when
        Bouquet saved = repository.save(b);
        //then
        Bouquet actual = repository.getReferenceById(saved.getId());
        assertEquals(b.getItemCode(), actual.getItemCode());
        assertEquals(b.getAvailableQuantity(), actual.getAvailableQuantity());
        assertEquals(b.getSoldQuantity(), actual.getSoldQuantity());
        assertEquals(b.getImageUrls(), actual.getImageUrls());
        assertEquals(b.getName().getOriginalText(), actual.getName().getOriginalText());
        assertEquals(b.getName().getTranslation().get(0).getText(), actual.getName().getTranslation().get(0).getText());
        assertEquals(b.getFlowers(), actual.getFlowers());
        assertEquals(b.getColors(), actual.getColors());
        assertEquals(b.getSizes(), actual.getSizes());


    }

}
