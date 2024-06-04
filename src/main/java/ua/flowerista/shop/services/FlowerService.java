package ua.flowerista.shop.services;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.models.Flower;
import ua.flowerista.shop.repositories.FlowerRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FlowerService {
    private static final Logger logger = LoggerFactory.getLogger(FlowerService.class);
    private final FlowerRepository flowerRepository;
    private final TextContentService textContentService;

    public void insert(Flower flower) {
        flowerRepository.save(flower);
    }

    public void deleteById(Integer id) {
        flowerRepository.deleteById(id);
    }

    @Cacheable("flowers")
    public List<Flower> getAll() {
        return flowerRepository.findAll();
    }

    public List<Flower> getAllUncached() {
        return flowerRepository.findAll();
    }

    public Optional<Flower> getById(Integer id) {
        return flowerRepository.findById(id);
    }

    public void update(Flower flower) {
        flowerRepository.save(flower);
    }

    public Page<Flower> getAll(Predicate predicate, Pageable pageable) {
        return flowerRepository.findAll(predicate, pageable);
    }

    public boolean isNameExist(String name) {
        return flowerRepository.existsByName(name);
    }

    public Flower addFlower(String name) {
        if (isNameExist(name)) {
            logger.error("Flower with name {} already exists", name);
            throw new AppException("Flower with name " + name + " already exists", HttpStatus.BAD_REQUEST);
        }
        Flower flower = new Flower();
        flower.setName(textContentService.getNewTextContent(name));
        return flowerRepository.save(flower);
    }
}
