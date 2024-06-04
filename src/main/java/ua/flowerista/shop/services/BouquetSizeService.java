package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.models.BouquetSize;
import ua.flowerista.shop.repositories.BouquetSizeRepository;

import java.math.BigInteger;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BouquetSizeService {
    private static final Logger logger = LoggerFactory.getLogger(BouquetSizeService.class);

    private final BouquetSizeRepository repository;

    public BouquetSize getById(Integer sizeId) {
        return repository.findById(sizeId).orElseThrow(() -> {
            logger.error("Bouquet size not found with id: {}", sizeId);
            return new AppException("Bouquet size not found with id: " + sizeId, HttpStatus.BAD_REQUEST);
        });
    }

    public Boolean isExistById(Integer sizeId) {
        return repository.existsById(sizeId);
    }

    public BigInteger getPriceById(Integer sizeId) {
        return repository.getPriceById(sizeId);
    }

    public void saveAll(Set<BouquetSize> bouquetSize) {
        repository.saveAll(bouquetSize);
    }
}
