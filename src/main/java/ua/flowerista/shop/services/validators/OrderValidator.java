package ua.flowerista.shop.services.validators;

import org.springframework.stereotype.Service;
import ua.flowerista.shop.dto.OrderDto;
import ua.flowerista.shop.models.Order;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderValidator {
    public List<String> validateOrder(OrderDto order) {
        List<String> errors = new ArrayList<>();
        return errors;
    }
}
