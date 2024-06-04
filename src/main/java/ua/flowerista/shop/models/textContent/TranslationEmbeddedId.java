package ua.flowerista.shop.models.textContent;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Embeddable
public class TranslationEmbeddedId implements java.io.Serializable {
    @ManyToOne(cascade = {jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE})
    @JoinColumn(name = "text_content_id")
    private TextContent textContent;

    @ManyToOne(cascade = {jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE})
    @JoinColumn(name = "language_id")
    private Language language;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranslationEmbeddedId that = (TranslationEmbeddedId) o;

        if (!textContent.equals(that.textContent)) return false;
        return language.equals(that.language);
    }

    @Override
    public int hashCode() {
        int result = textContent.hashCode();
        result = 31 * result + language.hashCode();
        return result;
    }
}
