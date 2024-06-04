package ua.flowerista.shop.mappers;

import org.mapstruct.Mapper;
import ua.flowerista.shop.dto.user.SignUpDto;
import ua.flowerista.shop.dto.user.UserDto;
import ua.flowerista.shop.dto.user.UserProfileDto;
import ua.flowerista.shop.models.user.User;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface UserMapper {

    User toEntity(UserDto dto);

    UserDto toDto(User entity);

    User toEntity(SignUpDto dto);

    UserProfileDto toProfileDto(User entity);
}
