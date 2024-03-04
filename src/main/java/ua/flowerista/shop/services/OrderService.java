package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.OrderItem;
import ua.flowerista.shop.models.OrderStatus;
import ua.flowerista.shop.repo.OrderItemRepository;
import ua.flowerista.shop.repo.OrderRepository;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public void updateStatus(Integer orderId, OrderStatus status) {
        orderRepository.updateStatus(orderId, status);
    }

    public void updatePayId(Integer orderId, String payId) {
        orderRepository.updatePayId(orderId, payId);
    }

    public Order createOrder(Order order) {
        order.setCurrency(Objects.requireNonNullElse(order.getCurrency(),"UAH"));
        Set<OrderItem> orderItems = order.getOrderItems().stream()
                .map(orderItemRepository::save)
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        order.setSum(orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigInteger.valueOf(orderItem.getQuantity())))
                .reduce(BigInteger.ZERO, BigInteger::add));
        return orderRepository.save(order);
    }

    public Optional<Order> getOrder(Integer id) {
        return orderRepository.findById(id);
    }

    public boolean isOrderExists(Integer orderId) {
        if (orderId == null) {
            return false;
        }
        return orderRepository.existsById(orderId);
    }

    public void updateStatusByPayId(String payId, OrderStatus status) {
        orderRepository.updateStatusByPayId(payId, status);
    }

    public boolean isOrderPayed(Integer orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            //TODO: trow exception for not found order
            return true;
        }
        return order.get().getStatus().compareTo(OrderStatus.PENDING) > 0;
    }
}
