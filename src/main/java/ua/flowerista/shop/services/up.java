package ua.flowerista.shop.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.tc.*;
import ua.flowerista.shop.models.tc.Bouquet;
import ua.flowerista.shop.models.tc.BouquetSize;
import ua.flowerista.shop.models.tc.Color;
import ua.flowerista.shop.models.tc.Flower;
import ua.flowerista.shop.repo.*;
import ua.flowerista.shop.repo.tc.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class up {
    private final BouquetSizeRepository bouquetSizeRepository;
    private final BouquetRepository bouquetRepository;
    private final ColorRepository colorRepository;
    private final FlowerRepository flowerRepository;

    private final BTR btr;
    private final BZTR bztr;
    private final CR cr;
    private final FR fr;
    private final TranslationReposytory tsr;
    private final TranslateRepository tr;
    private final TextContentRepository tcr;
    private final LanguageRepository lr;

    @PostConstruct
    @Transactional
    public void init(){
//        createLanguages();
//        migrateFlower();
//        migrateColor();
        migrateBouquet();
        System.out.println("done");
    }

    private void createLanguages() {
        Language en = new Language();
        en.setName("en");
        lr.save(en);

        Language ua = new Language();
        ua.setName("uk");
        lr.save(ua);
    }

    private void migrateBouquet() {
        for (ua.flowerista.shop.models.Bouquet bouquet : bouquetRepository.findAll()) {
            Bouquet b = new Bouquet();

            b.setItemCode(bouquet.getItemCode());
            b.setAvailableQuantity(bouquet.getQuantity());
            b.setSoldQuantity(bouquet.getSoldQuantity());
            b.setImageUrls(bouquet.getImageUrls());

            TextContent textContent = new TextContent();
            textContent.setOriginalText(bouquet.getName());
            textContent.setOriginalLanguage(lr.findByName("en"));
            Set<Translation> translations = new HashSet<>();
            bouquet.getTranslates().forEach((t) -> {
                Translation translation = new Translation();
                TranslationEmbeddedId translationEmbeddedId = new TranslationEmbeddedId();
                Language language = lr.findByName(t.getLanguage().name());
                translationEmbeddedId.setLanguage(language);
                translationEmbeddedId.setTextContent(textContent);

                translation.setTranslationEmbeddedId(translationEmbeddedId);
                translation.setText(t.getText());

                translations.add(translation);
            });
            textContent.setTranslation(translations);
            b.setName(textContent);

            Set<Flower> flowers = new HashSet<>();
            bouquet.getFlowers().forEach((f) -> {
                Flower nFlower = fr.findByName(f.getName());
                flowers.add(nFlower);
            });
            b.setFlowers(flowers);

            Set<Color> colors = new HashSet<>();
            bouquet.getColors().forEach((c) -> {
                Color nColor = cr.findByName(c.getName());
                colors.add(nColor);
            });
            b.setColors(colors);

            Set<BouquetSize> bouquetSizes = new HashSet<>();
            bouquet.getSizes().forEach((bs) -> {
                BouquetSize bz = new BouquetSize();
                bz.setDefaultPrice(bs.getDefaultPrice());
                bz.setIsSale(bs.getIsSale());
                bz.setDiscountPrice(bs.getDiscountPrice());
                bz.setBouquet(b);
                bouquetSizes.add(bz);
            });
            b.setSizes(bouquetSizes);

            btr.save(b);
        }
    }

    private void migrateColor() {
        for (ua.flowerista.shop.models.Color color : colorRepository.findAll()) {
            Color c = new Color();

            TextContent textContent = new TextContent();
            textContent.setOriginalText(color.getName());
            textContent.setOriginalLanguage(lr.findByName("en"));
            Set<Translation> translations = new HashSet<>();
            color.getNameTranslate().forEach((t) -> {
                TranslationEmbeddedId translationEmbeddedId = new TranslationEmbeddedId();
                Language language = lr.findByName(t.getLanguage().name());
                translationEmbeddedId.setLanguage(language);
                translationEmbeddedId.setTextContent(textContent);

                Translation translation = new Translation();
                translation.setTranslationEmbeddedId(translationEmbeddedId);
                translation.setText(t.getText());
                translations.add(translation);

            });
            textContent.setTranslation(translations);
            c.setName(textContent);
            cr.save(c);
        }
    }

    private void migrateFlower() {
        for (ua.flowerista.shop.models.Flower flower : flowerRepository.findAll()) {
            Flower f = new Flower();

            TextContent textContent = new TextContent();
            textContent.setOriginalText(flower.getName());
            textContent.setOriginalLanguage(lr.findByName("en"));
            Set<Translation> translations = new HashSet<>();
            flower.getNameTranslate().forEach((t) -> {
                Translation translation = new Translation();
                TranslationEmbeddedId translationEmbeddedId = new TranslationEmbeddedId();
                Language language = lr.findByName(t.getLanguage().name());
                translationEmbeddedId.setLanguage(language);
                translationEmbeddedId.setTextContent(textContent);

                translation.setTranslationEmbeddedId(translationEmbeddedId);
                translation.setText(t.getText());

                translations.add(translation);
            });
            textContent.setTranslation(translations);
            f.setName(textContent);
            fr.save(f);
        }
    }

    public List<ua.flowerista.shop.models.Bouquet> getBouquets() {
        return bouquetRepository.findAll();
    }
    public List<ua.flowerista.shop.models.Color> getColors(){
        return colorRepository.findAll();
    }
    public List<ua.flowerista.shop.models.Flower> getFlowers(){
        return flowerRepository.findAll();
    }

//    @PostConstruct
//    public void init() {
//        List<Bouquet> bouquets = bouquetRepository.findAll();
//        bouquets.stream()
//                .map((o) -> {
//                    BouquetT n = new BouquetT();
//                    n.setId(o.getId());
//                    n.setItemCode(o.getItemCode());
//                    n.setQuantity(o.getQuantity());
//                    n.setSoldQuantity(o.getSoldQuantity());
//                    n.setImageUrls(o.getImageUrls());
//
//                    Set<F> flowers = new HashSet<>();
//                    o.getFlowers().forEach((f) -> {
//                        F nF = new F();
//                        nF.setId(f.getId());
//                        Set<Property> properties = new HashSet<>();
//                        f.getNameTranslate().forEach((t) -> {
//                            Property p = new Property();
//                            p.setText(t.getText());
//                            p.setLanguage(t.getLanguage());
//                            p.setTitle("name");
//                            properties.add(p);
//                        });
//                        nF.setProperties(properties);
//                        flowers.add(nF);
//                    });
//
//                    Set<Property> properties = new HashSet<>();
//                    o.getTranslates().forEach((t) -> {
//                        Property p = new Property();
//                        p.setText(t.getText());
//                        p.setLanguage(t.getLanguage());
//                        p.setTitle("name");
//                        properties.add(p);
//                    });
//                    n.setProperties(properties);
//
//
//
//
//                    return n;
//                })
//    }
}
