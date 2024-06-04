package ua.flowerista.shop.models;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;
import ua.flowerista.shop.models.textContent.TextContent;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "bouquets")
public class Bouquet {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Flower> flowers;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Color> colors;

	@Column(name = "item_code", unique = true)
	private String itemCode;

	@Type(JsonType.class)
	@Column(columnDefinition = "jsonb")
	private Map<Integer, String> imageUrls;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "bouquet")
    private Set<BouquetSize> sizes;

	@Column(name = "available_quantity")
	private int availableQuantity;

	@Column(name = "sold_quantity")
	private int soldQuantity;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "name_id")
	private TextContent name;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Bouquet bouquet = (Bouquet) o;
		return getId() != null && Objects.equals(getId(), bouquet.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}
