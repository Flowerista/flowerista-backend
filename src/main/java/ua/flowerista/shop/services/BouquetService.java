package ua.flowerista.shop.services;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.flowerista.shop.dto.BouquetDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.models.*;
import ua.flowerista.shop.repo.BouquetRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BouquetService {
    private static final Logger logger = LoggerFactory.getLogger(BouquetService.class);
    private final EntityManager entityManager;

    private final FlowerService flowerService;
    private final ColorService colorService;

    private final BouquetRepository bouquetRepository;
    private final CloudinaryService cloudinary;

    public Optional<Bouquet> getBouquetById(Integer id) {
        return bouquetRepository.findById(id);
    }

    public List<Bouquet> getBouquetsBestSellers() {
        return bouquetRepository.findTop5ByOrderBySoldQuantityDesc();
    }

    public List<Bouquet> getBouquetsTop5Sales() {
        return bouquetRepository.findTop5ByOrderByDiscountDesc();
    }

    public List<Bouquet> getBouquetsCatalogFiltered(List<Integer> flowerIds,
                                                    List<Integer> colorIds,
                                                    Integer minPrice,
                                                    Integer maxPrice,
                                                    Boolean sortByNewest,
                                                    Boolean sortByPriceHighToLow,
                                                    Boolean sortByPriceLowToHigh) {
        //cached query for all bouquets
        List<Bouquet> bouquets = bouquetRepository.findAll();
        //if all filters are null, return all bouquets
        if ((flowerIds == null) && (colorIds == null) && (minPrice == null) && (maxPrice == null)
                && (sortByNewest == null) && (sortByPriceHighToLow == null) && (sortByPriceLowToHigh == null)) {
            return bouquets;
        }
        //else return bouquets filtered by ids from db query with filters
        else {
            //get ids from db query with filters
            List<Integer> ids = bouquetRepository.findByFilters(flowerIds, colorIds, minPrice, maxPrice, sortByNewest,
                    sortByPriceHighToLow, sortByPriceLowToHigh);
            //get bouquets by ids from cached results
            return ids.stream()
                    .map(id -> bouquets.stream()
                            .filter(bouquete -> Objects.equals(bouquete.getId(), id))
                            .findFirst().orElse(null))
                    .collect(Collectors.toList());
        }
    }

    public Integer getMinPrice() {
        return bouquetRepository.findMinPrice();
    }

    public Integer getMaxPrice() {
        return bouquetRepository.findMaxPrice();
    }

    public List<Bouquet> searchBouquets(String name) {
        if (name == null || name.length() < 3) {
            return Collections.emptyList();
        }
        JPAQuery<Bouquet> query = new JPAQuery<>(entityManager);
        QBouquet bouquet = QBouquet.bouquet;
        return query
                .from(bouquet)
                .where(bouquet.translates.any().text.containsIgnoreCase(name))
                .fetch();
    }

    public Boolean isBouquetExist(Integer id) {
        return bouquetRepository.existsById(id);
    }


    public boolean isBouquetAvailableForSale(Integer productId) {
        return bouquetRepository.isBouquetAvailableForSale(productId);
    }

    public Page<Bouquet> getAllBouquets(Predicate predicate,
                                        Pageable pageable) {
        return bouquetRepository.findAll(predicate, pageable);
    }

    public void deleteImageFromBouquet(Integer bouquetId, Integer imageId) {
        Bouquet bouquet = bouquetRepository.findById(bouquetId).orElseThrow();
        Map<Integer, String> imageUrls = bouquet.getImageUrls();
        try {
            cloudinary.deleteImage(cloudinary.extractPublicId(imageUrls.get(imageId)));
        } catch (Exception e) {
            logger.error("Error deleting the image", e);
            throw new AppException("Error deleting the image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        imageUrls.remove(imageId);
        bouquet.setImageUrls(imageUrls);
        bouquetRepository.save(bouquet);
    }

    public void update(BouquetDto dto, Languages lang) {
        Bouquet entity = bouquetRepository.findById(dto.getId());

        if (lang == Languages.en) {
            entity.setName(dto.getName());
        }

        Set<Translate> translates = entity != null ? entity.getTranslates() : null;

        Translate translate;
        if (translates != null) {
            translate = translates.stream().filter(t -> t.getLanguage() == lang).findFirst().orElse(new Translate());
            translates.remove(translate);
        } else {
            translate = new Translate();
        }
        translate.setText(dto.getName());
        translate.setLanguage(lang);
//        translate.setBouquet(entity);
        translates.add(translate);
        entity.setTranslates(translates);
        entity.setFlowers(dto.getFlowers().stream()
                .map(flowerDto -> flowerService.getById(flowerDto.getId())
                        .orElseThrow(() -> {
                            logger.error("Flower not found {} ", flowerDto.getId());
                            return new AppException("Flower not found", HttpStatus.INTERNAL_SERVER_ERROR);
                        }))
                .collect(Collectors.toSet()));
        entity.setColors(dto.getColors().stream()
                .map(colorDto -> colorService.getById(colorDto.getId())
                        .orElseThrow(() -> {
                            logger.error("Color not found {} ", colorDto.getId());
                            return new AppException("Color not found", HttpStatus.INTERNAL_SERVER_ERROR);
                        }))
                .collect(Collectors.toSet()));
        bouquetRepository.save(entity);
    }

    public void updateStock(Set<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            Bouquet bouquet = bouquetRepository.getReferenceById(orderItem.getBouquet().getId());
            if (bouquet.getQuantity() < orderItem.getQuantity()) {
                throw new AppException("Not enough stock for product " + bouquet.getName(), HttpStatus.BAD_REQUEST);
            }
            bouquet.setQuantity(bouquet.getQuantity() - orderItem.getQuantity());
            bouquet.setSoldQuantity(bouquet.getSoldQuantity() + orderItem.getQuantity());
            bouquetRepository.save(bouquet);
        });
    }

    public void addImagesToBouquet(Integer id, List<MultipartFile> images) {
        Bouquet bouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new AppException("Bouquet not found", HttpStatus.INTERNAL_SERVER_ERROR));
        Map<Integer, String> imageUrls = bouquet.getImageUrls();
        int lastIndex = imageUrls.keySet().stream().max(Integer::compareTo).orElse(0);
        String imageUrl;
        for (int i = 0; i < images.size(); i++) {
            imageUrl = null;
            try {
                imageUrl = cloudinary.uploadImage(images.get(i));
            } catch (IOException e) {
                logger.error("Error uploading the image", e);
                throw new AppException("Error uploading the image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            imageUrls.put(lastIndex + i + 1, imageUrl);
        }
        bouquet.setImageUrls(imageUrls);
        bouquetRepository.save(bouquet);
    }
}
