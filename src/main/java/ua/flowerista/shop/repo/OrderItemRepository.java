package ua.flowerista.shop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
