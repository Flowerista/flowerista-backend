package ua.flowerista.shop.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Subscription {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "email", nullable = false, unique = true, length = 320)
	private String email;

}
