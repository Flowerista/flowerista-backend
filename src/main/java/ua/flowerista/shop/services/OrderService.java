package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.OrderStatus;
import ua.flowerista.shop.repo.OrderRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void updateStatus(Integer orderId, String status) {
        orderRepository.updateStatus(orderId, status);
    }

    public void updatePayId(Integer orderId, String payId) {
        orderRepository.updatePayId(orderId, payId);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> getOrder(Integer id) {
        return orderRepository.findById(id);
    }

    public boolean isOrderExists(Order order) {
        if (order.getId() == null) {
            return false;
        }
        return orderRepository.existsById(order.getId());
    }

    public void updateStatusByPayId(String payId, String status) {
        orderRepository.updateStatusByPayId(payId, status);
    }

    public boolean isOrderPayed(Integer orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return true;
        }
        return order.get().getStatus().compareTo(OrderStatus.PENDING) > 0;
    }
}
