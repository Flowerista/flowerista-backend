package ua.flowerista.shop.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "color")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Color {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name", unique = true)
	@NotBlank
	private String name;
	@ToString.Exclude
	@OneToMany(mappedBy = "color", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Translate> nameTranslate = new LinkedHashSet<>();

}
