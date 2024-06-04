package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.Color;
import ua.flowerista.shop.repositories.ColorRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository colorRepository;
    private final TextContentService textContentService;

    public void insert(Color color) {
        colorRepository.save(color);
    }

    public void deleteById(int id) {
        colorRepository.deleteById(id);
    }

    @Cacheable("colors")
    public List<Color> getAll() {
        return colorRepository.findAll();
    }

    public List<Color> getAllUncached() {
        return colorRepository.findAll();
    }

    public Optional<Color> getById(Integer id) {
        return colorRepository.findById(id);
    }

    public void update(Color color) {
        colorRepository.save(color);
    }

    public Color addColor(String name) {
        Color color = new Color();
        color.setName(textContentService.getNewTextContent(name));
        return colorRepository.save(color);
    }
}
