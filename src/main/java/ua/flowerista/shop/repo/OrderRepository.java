package ua.flowerista.shop.repo;

import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.OrderStatus;
import ua.flowerista.shop.models.QOrder;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, QuerydslPredicateExecutor<Order>, QuerydslBinderCustomizer<QOrder> {
    @Override
    default void customize(QuerydslBindings bindings, QOrder root){
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) (path, s) -> path.equalsIgnoreCase(s));
    }

    List<Order> findByUserId(Integer userId, Sort sort);

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
    @Modifying
    @Transactional
    @Query("update Order o set o.updated = :now where o.id = :orderId")
    void updateUpdatedDateTime(Integer orderId, Instant now);
}
