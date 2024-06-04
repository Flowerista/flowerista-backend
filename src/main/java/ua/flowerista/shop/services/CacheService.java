package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.repositories.BouquetRepository;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;
    private final BouquetRepository bouquetRepository;

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
        bouquetRepository.findAll();
    }

    @Scheduled(fixedRate = 5000)
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
    }
}
