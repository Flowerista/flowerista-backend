package ua.flowerista.shop.models.tc;

import jakarta.persistence.*;
import lombok.*;
import ua.flowerista.shop.models.Languages;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "lang", nullable = false)
    @Enumerated(EnumType.STRING)
    private Languages language;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false)
    private String text;

}
