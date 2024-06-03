package ua.flowerista.shop.services;

import com.querydsl.core.types.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.OrderItem;
import ua.flowerista.shop.models.OrderStatus;
import ua.flowerista.shop.models.PaymentOrder;
import ua.flowerista.shop.repo.*;

import java.time.Instant;
import java.util.List;
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
    private final PaymentOrderRepository paymentOrderRepository;

    private final BouquetService bouquetService;

    public void updateStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.getReferenceById(orderId);
        order.setStatus(status);
        order.setUpdated(Instant.now());
        orderRepository.save(order);
    }

    public void updatePayId(Integer orderId, String payId) {
        Order order = orderRepository.getReferenceById(orderId);
        order.setPayId(payId);
        orderRepository.save(order);
    }
    @Transactional
    public Order create(Order order) {
        order.setCreated(Instant.now());
        order.setStatus(OrderStatus.PLACED);
        bouquetService.updateStock(order.getOrderItems());
        return save(order);
    }
    @Transactional
    public Order update(Order order) {
        if (order.getStatus().compareTo(OrderStatus.PLACED) != 0) {
            throw new AppException("Order can't be updated. Already in processing...", HttpStatus.BAD_REQUEST);
        }
        order.setUpdated(Instant.now());
        return save(order);
    }


    public Optional<Order> getById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    public boolean isExists(Integer orderId) {
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
            //not exists order can't be payed
            return false;
        }
        PaymentOrder paymentOrder = paymentOrderRepository.findByPayId(order.get().getPayId());
        return paymentOrder != null && paymentOrder.getStatus().equals("payed");
    }

    public boolean isOrderWaitingForPayment(Integer orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return false;
        }
        return order.get().getStatus().equals(OrderStatus.PENDING);
    }

    public Page<Order> getAll(Predicate predicate,
                              Pageable pageable) {
        //TODO: need to increase performance
        return orderRepository.findAll(predicate, pageable);
    }

    public List<Order> getByUserId(Integer userId) {
        return orderRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "created"));
    }

    private Order save(Order order) {
        order.setCurrency(Objects.requireNonNullElse(order.getCurrency(), "USD"));
        Set<OrderItem> orderItems = order.getOrderItems().stream()
                .map(orderItemRepository::save)
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        order.setAddress(addressRepository.save(order.getAddress()));
        return orderRepository.save(order);
    }

    public Integer getUserId(Integer orderId) {
        return orderRepository.findUserIdByOrderId(orderId);
    }
}
