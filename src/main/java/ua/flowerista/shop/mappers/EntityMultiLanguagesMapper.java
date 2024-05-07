package ua.flowerista.shop.mappers;

import ua.flowerista.shop.models.Languages;

public interface EntityMultiLanguagesMapper<E, D> {
    E toEntity(D dto, Languages language);

    D toDto(E entity, Languages language);
}
