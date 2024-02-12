package ua.flowerista.shop.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@ToString
@Table(name = "bouquete_size")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class BouqueteSize {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "size")
	@Enumerated(EnumType.STRING)
	private Size size;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "bouquete_id", nullable = false)
	private Bouquete bouquete;

	@Column(name = "defaultprice")
	@NotNull
	private int defaultPrice;

	@Column(name = "discount")
	private Integer discount;

	@Column(name = "discountprice")
	private Integer discountPrice;

}
