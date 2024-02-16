package ua.flowerista.shop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Modifying
    @Transactional
    @Query("update Order o set o.status = :status where o.id = :id")
    void updateStatus(@Param("id") Integer id, @Param("status") OrderStatus status);

    @Modifying
    @Transactional
    @Query("update Order o set o.payId = :payId where o.id = :orderId")
    void updatePayId(@Param("orderId") Integer orderId, @Param("payId") String payId);

    @Modifying
    @Transactional
    @Query("update Order o set o.status = :status where o.payId = :payId")
    void updateStatusByPayId(@Param("payId") String payId, @Param("status") OrderStatus status);
}
