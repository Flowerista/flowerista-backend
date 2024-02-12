package ua.flowerista.shop.mappers;

public interface EntityMapper<E, D> {

	E toEntity(D dto);

	D toDto(E toEntity);

}
