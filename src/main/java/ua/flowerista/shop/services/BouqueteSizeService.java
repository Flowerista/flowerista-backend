package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.BouquetSize;
import ua.flowerista.shop.repo.BouquetSizeRepository;

import java.math.BigInteger;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BouqueteSizeService {
    private final BouquetSizeRepository repository;

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
