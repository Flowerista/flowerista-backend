package ua.flowerista.shop.services.validators;

import org.springframework.stereotype.Service;
import ua.flowerista.shop.dto.OrderDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderValidator {
    public List<String> validateOrder(OrderDto order) {
        List<String> errors = new ArrayList<>();
        //TODO: implement validation
        return errors;
    }
}
