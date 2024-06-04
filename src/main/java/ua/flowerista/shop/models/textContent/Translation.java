package ua.flowerista.shop.models.textContent;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Translation {
    @EmbeddedId
    private TranslationEmbeddedId translationEmbeddedId;

    @Column(name = "text", nullable = false)
    private String text;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Translation that = (Translation) o;
        return getTranslationEmbeddedId() != null && Objects.equals(getTranslationEmbeddedId(), that.getTranslationEmbeddedId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(translationEmbeddedId);
    }
}
