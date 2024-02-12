package ua.flowerista.shop.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name = "order_item")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OrderItem {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
    @NotEmpty
    @Column(name = "product_id")
    private int productId;
	@Column(name = "name", nullable = false, unique = true)
	@NotBlank
	private String name;
	@Column(name = "quantity")
	private int quantity;
	@Column(name = "price")
	private int price;

}
