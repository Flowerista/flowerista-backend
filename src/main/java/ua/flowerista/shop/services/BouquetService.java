package ua.flowerista.shop.services;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.models.Bouquet;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.models.QBouquet;
import ua.flowerista.shop.models.order.OrderItem;
import ua.flowerista.shop.repositories.BouquetRepository;

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

    public Optional<Bouquet> getById(Integer id) {
        return bouquetRepository.findById(id);
    }

    public Bouquet findById(Integer id) {
        return bouquetRepository.findById(id).orElseThrow(() -> {
            logger.error("Bouquet not found {} ", id);
            return new AppException("Bouquet not found", HttpStatus.INTERNAL_SERVER_ERROR);
        });
    }

    public List<Bouquet> getBestSellers() {
        return bouquetRepository.findTop5ByOrderBySoldQuantityDesc();
    }

    public List<Bouquet> getTop5Sales() {
        return bouquetRepository.findTop5ByOrderByDiscountDesc();
    }

    @Cacheable("bouquets")
    public List<Bouquet> findAll() {
        return bouquetRepository.findAll();
    }

    public List<Bouquet> finnAllUncached() {
        return bouquetRepository.findAll();
    }

    public List<Bouquet> getCatalogFiltered(List<Integer> flowerIds,
                                            List<Integer> colorIds,
                                            Integer minPrice,
                                            Integer maxPrice,
                                            Boolean sortByNewest,
                                            Boolean sortByPriceHighToLow,
                                            Boolean sortByPriceLowToHigh) {
        //cached query for all bouquets
        List<Bouquet> bouquets = findAll();
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
                            .filter(bouquet -> Objects.equals(bouquet.getId(), id))
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

    public List<Bouquet> search(String name) {
        if (name == null || name.length() < 3) {
            return Collections.emptyList();
        }
        JPAQuery<Bouquet> query = new JPAQuery<>(entityManager);
        QBouquet bouquet = QBouquet.bouquet;
        return query
                .from(bouquet)
                .where(bouquet.name.translation.any().text.containsIgnoreCase(name))
                .fetch();
    }

    public Boolean isExist(Integer id) {
        return bouquetRepository.existsById(id);
    }


    public boolean isAvailableForSale(Integer productId) {
        return bouquetRepository.isBouquetAvailableForSale(productId);
    }

    public Page<Bouquet> getAll(Predicate predicate,
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

    public void updateStock(Set<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            Bouquet bouquet = bouquetRepository.getReferenceById(orderItem.getBouquet().getId());
            if (bouquet.getAvailableQuantity() < orderItem.getQuantity()) {
                throw new AppException("Not enough stock for product " + bouquet.getName(), HttpStatus.BAD_REQUEST);
            }
            bouquet.setAvailableQuantity(bouquet.getAvailableQuantity() - orderItem.getQuantity());
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
            try {
                imageUrl = cloudinary.uploadImage(images.get(i));
                imageUrls.put(lastIndex + i + 1, imageUrl);
            } catch (IOException e) {
                logger.error("Error uploading the image", e);
                throw new AppException("Error uploading the image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        bouquet.setImageUrls(imageUrls);
        bouquetRepository.save(bouquet);
    }

    public void save(Bouquet bouquet) {
        try {
            bouquetRepository.save(bouquet);
        } catch (Exception e) {
            logger.error("Error saving the bouquet", e);
            throw new AppException("Error saving the bouquet " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public void updateFlowers(Integer id, List<Integer> flowerIds) {
        Bouquet bouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new AppException("Bouquet not found", HttpStatus.INTERNAL_SERVER_ERROR));
        Set<Flower> flowers = bouquet.getFlowers();
        flowerIds.forEach(flowerId -> {
            Flower flower = flowerService.getById(flowerId)
                    .orElseThrow(() -> {
                        logger.error("Flower not found {} ", flowerId);
                        return new AppException("Flower not found", HttpStatus.INTERNAL_SERVER_ERROR);
                    });
            flowers.add(flower);
        });
        bouquet.setFlowers(flowers);
        bouquetRepository.save(bouquet);
    }

    public void updateColors(Integer id, List<Integer> colorIds) {
        Bouquet bouquet = bouquetRepository.findById(id)
                .orElseThrow(() -> new AppException("Bouquet not found", HttpStatus.INTERNAL_SERVER_ERROR));
        Set<Color> colors = bouquet.getColors();
        colorIds.forEach(colorId -> {
            Color color = colorService.getById(colorId)
                    .orElseThrow(() -> {
                        logger.error("Color not found {} ", colorId);
                        return new AppException("Color not found", HttpStatus.INTERNAL_SERVER_ERROR);
                    });
            colors.add(color);
        });
        bouquet.setColors(colors);
        bouquetRepository.save(bouquet);
    }
}
