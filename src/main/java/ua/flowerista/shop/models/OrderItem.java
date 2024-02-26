package ua.flowerista.shop.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "order_item")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OrderItem {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
    @ManyToOne
	@JoinColumn(name = "bouquete_id")
	private Bouquete bouquete;
	@Column(name = "name")
	@NotBlank
	private String name;
	@Column(name = "quantity")
	private int quantity;
	@ManyToOne
	@JoinColumn(name = "size_id")
	private BouqueteSize size;
	@ManyToOne
	@JoinColumn(name = "color_id")
	private Color color;
	@Column(name = "price")
	private int price;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		OrderItem orderItem = (OrderItem) o;
		return getId() != null && Objects.equals(getId(), orderItem.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}
