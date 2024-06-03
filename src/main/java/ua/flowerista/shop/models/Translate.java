package ua.flowerista.shop.models;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "lang", nullable = false)
    @Enumerated(EnumType.STRING)
    private Languages language;

//    @Column(name = "title", nullable = false)
//    private String title;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id")
    private Flower flower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bouquete_id")
    private Bouquet bouquet;

}
