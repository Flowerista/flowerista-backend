package ua.flowerista.shop.mappers;

import ua.flowerista.shop.models.Languages;

public interface EntityMultiLanguagesDtoMapper<E, D> {
    D toDto(E entity, Languages language);
}
