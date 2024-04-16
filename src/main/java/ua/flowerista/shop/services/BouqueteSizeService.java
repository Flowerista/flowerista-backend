package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.BouqueteSize;
import ua.flowerista.shop.repo.BouqueteSizeRepository;

import java.math.BigInteger;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BouqueteSizeService {
    private final BouqueteSizeRepository repository;

    public Boolean isSizeExist(Integer sizeId) {
        return repository.existsById(sizeId);
    }

    public BigInteger getPrice(Integer sizeId) {
        return repository.getPriceById(sizeId);
    }

    public void saveAll(Set<BouqueteSize> bouqueteSize) {
        repository.saveAll(bouqueteSize);
    }
}
