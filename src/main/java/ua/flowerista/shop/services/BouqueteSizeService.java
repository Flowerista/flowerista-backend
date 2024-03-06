package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.repo.BouqueteSizeRepository;

import java.math.BigInteger;

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
}
