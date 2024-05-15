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

    @ManyToOne
    @JoinColumn(name = "bouquete_id")
    private Bouquete bouquete;

    @Column(name = "lang", nullable = false)
    @Enumerated(EnumType.STRING)
    private Languages language;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "flower_id")
    private Flower flower;

}
