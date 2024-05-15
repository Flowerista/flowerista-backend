package ua.flowerista.shop.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigInteger;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@ToString
@Table(name = "bouquete_size")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BouqueteSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private Size size;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "bouquete_id", nullable = false)
    @ToString.Exclude
    private Bouquete bouquete;

    @Column(name = "defaultprice")
    @NotNull
    private BigInteger defaultPrice;

    @Column(name = "is_sale")
    private Boolean isSale;

    @Column(name = "discount")
    private BigInteger discount;

    @Column(name = "discountprice")
    private BigInteger discountPrice;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BouqueteSize that = (BouqueteSize) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
