package ua.flowerista.shop.repositories.paypal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.flowerista.shop.models.paypal.PaymentOrder;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
    PaymentOrder findByPayId(String payId);
    @Modifying
    @Transactional
    @Query("update PaymentOrder p set p.status = ?2, p.dateOfPayment = ?3 where p.payId = ?1")
    void updateStatusAndDateOfPaymentByPayId(String payId, String status, String dateOfPayment);
}
