package ua.flowerista.shop.services.validators;

import com.cloudinary.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.dto.FlowerDto;
import ua.flowerista.shop.services.FlowerService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowerValidator {
    private final FlowerService flowerService;

    public List<String> validate(FlowerDto flower) {
        List<String> errors = new ArrayList<>();

        isNameEmpty(flower, errors);
        isNameUnique(flower, errors);

        return errors;
    }

    private void isNameEmpty(FlowerDto flower, List<String> errors) {
        if (StringUtils.isEmpty(flower.getName())) {
            errors.add("Name should not be empty");
        }
    }

    private void isNameUnique(FlowerDto flower, List<String> errors) {
        if (flowerService.isNameExist(flower.getName())) {
            errors.add("Name should be unique");
        }
    }
}