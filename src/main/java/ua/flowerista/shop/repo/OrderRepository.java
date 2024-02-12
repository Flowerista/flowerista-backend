package ua.flowerista.shop.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.flowerista.shop.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Modifying
    @Query("update Order o set o.status = :status where o.id = :id")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Modifying
    @Query("update Order o set o.payId = :id where o.id = :orderId")
    void updatePayId(@Param("payId") Integer orderId, @Param("id") String id);

    @Query("select o.id from Order o where o.payId = :token")
    Integer getOrderIdByPayId(String token);
}
