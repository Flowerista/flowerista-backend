package ua.flowerista.shop.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "bouquete")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Bouquete {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
			CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinTable(name = "bouquete_flower", joinColumns = @JoinColumn(name = "bouquete_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "flower_id"))
	private Set<Flower> flowers;
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
			CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinTable(name = "bouquete_color", joinColumns = @JoinColumn(name = "bouquete_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "color_id"))
	private Set<Color> colors;
	@Column(name = "itemcode", nullable = false, unique = true)
	@NotBlank
	private String itemCode;
	@Column(name = "name", nullable = false, unique = true)
	@NotBlank
	private String name;
	@Type(JsonType.class)
	@Column(columnDefinition = "jsonb")
	private Map<Integer, String> imageUrls;
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "bouquete", fetch = FetchType.EAGER)
    private Set<BouqueteSize> sizes;
	@Column(name = "quantity")
	private int quantity;
	@Column(name = "soldquantity")
	private int soldQuantity;
	@ToString.Exclude
	@OneToMany(mappedBy = "bouquete", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Translate> translates = new LinkedHashSet<>();

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Bouquete bouquete = (Bouquete) o;
		return getId() != null && Objects.equals(getId(), bouquete.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}
