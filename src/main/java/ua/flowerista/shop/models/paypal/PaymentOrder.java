package ua.flowerista.shop.models.paypal;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_orders")
public class PaymentOrder implements Serializable {
    @Column(name = "status")
    private String status;
    @Id
    @Column(name = "pay_id")
    private String payId;
    @Column(name = "redirect_url")
    private String redirectUrl;
    @Column(name = "date_of_payment")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dateOfPayment;

    public PaymentOrder(String status) {
        this.status = status;
    }

    public PaymentOrder(String success, String id, String redirectUrl) {
        this.status = success;
        this.payId = id;
        this.redirectUrl = redirectUrl;
    }
}
