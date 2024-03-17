package ua.flowerista.shop.services;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.OrderItem;
import ua.flowerista.shop.models.OrderStatus;
import ua.flowerista.shop.repo.AddressRepository;
import ua.flowerista.shop.repo.OrderItemRepository;
import ua.flowerista.shop.repo.OrderRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;

    public void updateStatus(Integer orderId, OrderStatus status) {
        orderRepository.updateStatus(orderId, status);
    }

    public void updatePayId(Integer orderId, String payId) {
        orderRepository.updatePayId(orderId, payId);
    }

    public Order createOrder(Order order) {
        order.setCurrency(Objects.requireNonNullElse(order.getCurrency(), "USD"));
        Set<OrderItem> orderItems = order.getOrderItems().stream()
                .map(orderItemRepository::save)
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        order.setAddress(addressRepository.save(order.getAddress()));
        order = orderRepository.save(order);
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

    public boolean isOrderWaitingForPayment(Integer orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return false;
        }
        return order.get().getStatus().equals(OrderStatus.PENDING);
    }

    public Page<Order> getAllOrders(Predicate predicate,
                                    Pageable pageable) {
        //TODO: need to increase performance
        return orderRepository.findAll(predicate, pageable);
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return getAllOrders().stream().filter(order -> order.getUserId() == userId).collect(Collectors.toList());
    }

    public void updateOrder(Integer id, Order entity) {
        if (entity.getId() == null) {
            entity.setId(id);
        }
        orderRepository.save(entity);
    }
}
