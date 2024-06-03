package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.repo.ColorRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository repo;

    public void insert(Color color) {
        repo.save(color);
    }

    public void deleteById(int id) {
        repo.deleteById(id);
    }

    public List<Color> getAll() {
        return repo.findAll();
    }

    public Optional<Color> getById(Integer id) {
        return repo.findById(id);
    }

    public void update(Color color) {
        repo.save(color);
    }
}
