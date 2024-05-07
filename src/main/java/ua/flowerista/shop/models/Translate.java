package ua.flowerista.shop.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@Builder
@Entity
@Table(name = "translate")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Translate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bouquete_id", nullable = false)
    private Bouquete bouquete;

    @Column(name = "lang", nullable = false)
    @Enumerated(EnumType.STRING)
    private Languages language;

    @Column(name = "text", nullable = false)
    private String text;

}
