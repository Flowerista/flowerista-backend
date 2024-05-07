package ua.flowerista.shop.models;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "bouquete")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Bouquete {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
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
	@OneToMany(mappedBy = "bouquete", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Translate> translates = new LinkedHashSet<>();

}
