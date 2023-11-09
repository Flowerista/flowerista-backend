package ua.flowerista.shop.mappers;

import org.springframework.stereotype.Component;

import ua.flowerista.shop.dto.UserDto;
import ua.flowerista.shop.dto.UserRegistrationBodyDto;
import ua.flowerista.shop.models.Role;
import ua.flowerista.shop.models.User;

@Component
public class UserMapper implements EntityMapper<User, UserDto> {

	@Override
	public User toEntity(UserDto dto) {
		User user = new User();
		user.setId(dto.getId());
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setEmail(dto.getEmail());
		user.setPhoneNumber(String.valueOf(dto.getPhoneNumber()));
		user.setRole(Role.valueOf(dto.getRole()));
		return user;
	}

	@Override
	public UserDto toDto(User entity) {
		UserDto dto = new UserDto();
		dto.setId(entity.getId());
		dto.setFirstName(entity.getLastName());
		dto.setLastName(entity.getLastName());
		dto.setEmail(entity.getEmail());
		dto.setPhoneNumber(Integer.valueOf(entity.getPhoneNumber()));
		dto.setRole(entity.getRole().toString());
		return null;
	}
	
	public User toEntity (UserRegistrationBodyDto dto) {
		User user = new User();
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setPhoneNumber(String.valueOf(dto.getPhoneNumber()));
		return user;
	}

}
