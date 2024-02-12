package ua.flowerista.shop.services;

import java.security.Principal;
import java.util.Calendar;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ua.flowerista.shop.dto.AddressDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.dto.user.UserChangePasswordRequestDto;
import ua.flowerista.shop.dto.user.UserChangePersonalInfoDto;
import ua.flowerista.shop.dto.user.UserLoginBodyDto;
import ua.flowerista.shop.dto.user.UserPasswordResetDto;
import ua.flowerista.shop.dto.user.UserProfileDto;
import ua.flowerista.shop.dto.user.UserRegistrationBodyDto;
import ua.flowerista.shop.exceptions.UserAlreadyExistException;
import ua.flowerista.shop.mappers.AddressMapper;
import ua.flowerista.shop.mappers.BouqueteMapper;
import ua.flowerista.shop.mappers.UserMapper;
import ua.flowerista.shop.models.Address;
import ua.flowerista.shop.models.Bouquete;
import ua.flowerista.shop.models.PasswordResetToken;
import ua.flowerista.shop.models.Role;
import ua.flowerista.shop.models.User;
import ua.flowerista.shop.models.VerificationToken;
import ua.flowerista.shop.repo.BouqueteRepository;
import ua.flowerista.shop.repo.PasswordResetTokenRepository;
import ua.flowerista.shop.repo.UserRepository;
import ua.flowerista.shop.repo.VerificationTokenRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private BouqueteRepository bouqueteRepository;

	@Autowired
	private UserMapper mapper;

	@Autowired
	private AddressMapper addressMapper;
	
	@Autowired
	private BouqueteMapper bouqueteMapper;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private VerificationTokenRepository tokenRepository;

	@Autowired
	private PasswordResetTokenRepository passwordTokenRepository;

	public static final String TOKEN_INVALID = "invalidToken";
	public static final String TOKEN_EXPIRED = "expired";
	public static final String TOKEN_VALID = "valid";

	public User registerNewUserAccount(UserRegistrationBodyDto regDto) {
		if (existsByEmail(regDto.getEmail())) {
			throw new UserAlreadyExistException("There is an account with that email address: " + regDto.getEmail());
		}
		if (existsByPhoneNumber(regDto.getPhoneNumber())) {
			throw new UserAlreadyExistException(
					"There is an account with that phone number: " + String.valueOf(regDto.getPhoneNumber()));
		}
		User user = mapper.toEntity(regDto);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(Role.USER);
		user.setAddress(new Address());
		return repository.save(user);
	}

	public Authentication userLogIn(UserLoginBodyDto logDto) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(logDto.getEmail(), logDto.getPassword()));
		return authentication;
	}

	public boolean existsByEmail(String email) {
		return repository.existsByEmail(email);
	}

	public boolean existsByPhoneNumber(int phoneNumber) {
		return repository.existsByPhoneNumber(String.valueOf(phoneNumber));
	}

	public void createVerificationTokenForUser(final User user, final String token) {
		final VerificationToken myToken = new VerificationToken(token, user);
		tokenRepository.save(myToken);
	}

	public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
		VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
		vToken.updateToken(UUID.randomUUID().toString());
		vToken = tokenRepository.save(vToken);
		return vToken;
	}

	public String validateVerificationToken(String token) {
		final VerificationToken verificationToken = tokenRepository.findByToken(token);
		if (verificationToken == null) {
			return TOKEN_INVALID;
		}

		final User user = verificationToken.getUser();
		final Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			tokenRepository.delete(verificationToken);
			repository.delete(user);
			return TOKEN_EXPIRED;
		}

		user.setEnabled(true);
		tokenRepository.delete(verificationToken);
		repository.save(user);
		return TOKEN_VALID;
	}

	public User findUserByEmail(final String email) {
		if (repository.findByEmail(email).isPresent()) {
			return repository.findByEmail(email).get();
		}
		return new User();
	}

	public void createPasswordResetTokenForUser(final User user, final String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordTokenRepository.save(myToken);
	}

	public PasswordResetToken getPasswordResetToken(final String token) {
		return passwordTokenRepository.findByToken(token);
	}

	public Optional<User> getUserByPasswordResetToken(final String token) {
		return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
	}

	public String resetPassword(UserPasswordResetDto dto) {
		if (dto.getPassword().equals(dto.getPasswordRepeated()) == false) {
			return "Passwords not matching";
		}
		String validatedToken = validatePasswordResetToken(dto.getToken());
		if (validatedToken != null) {
			return validatedToken;
		}
		Optional<User> user = Optional.ofNullable(passwordTokenRepository.findByToken(dto.getToken()).getUser());

		if (user.isPresent()) {
			final PasswordResetToken token = passwordTokenRepository.findByToken(dto.getToken());
			user.get().setPassword(passwordEncoder.encode(dto.getPasswordRepeated()));
			repository.save(user.get());
			passwordTokenRepository.delete(token);
			return "Password changed";
		}

		return "Something went wrong";
	}

	public UserProfileDto getUserDto(Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		if (user != null) {
			return mapper.toProfileDto(repository.findByEmail(user.getEmail()).get());
		}
		return new UserProfileDto();
	}

	public String changePassword(UserChangePasswordRequestDto request, Principal connectedUser) {

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		if (request.getNewPassword() == null) {
			return "New password cannot be null";
		}
		if (request.getCurrentPassword() == null) {
			return "Current password cannot be null";
		}

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			return "Wrong password";
		}
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		repository.save(user);
		return "Password changed";
	}

	public void changeAddress(AddressDto address, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		Address addressEntity = addressMapper.toEntity(address);
		addressEntity.setId(user.getAddress().getId());
		user.setAddress(addressEntity);
		repository.save(user);

	}

	public void changePersonalInfo(UserChangePersonalInfoDto dto, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		repository.save(user);
	}
	
	public Set<BouqueteSmallDto> getWishList (Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		return user.getWishlist().stream().map(bouquete -> bouqueteMapper.toSmallDto(bouquete)).collect(Collectors.toSet());
	}
	
	public void addBouqueteToWishList (int id, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		Bouquete bouquete = bouqueteRepository.getReferenceById(id);
		user.getWishlist().add(bouquete);
		repository.save(user);
	}
	
	public void deleteBouqueteFromWishList (int id, Principal connectedUser) {
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		Bouquete bouquete = bouqueteRepository.getReferenceById(id);
		user.getWishlist().remove(bouquete);
		repository.save(user);
	}	

	private String validatePasswordResetToken(String token) {
		final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

		return !isTokenFound(passToken) ? "invalidToken" : isTokenExpired(passToken) ? "expired" : null;
	}

	private boolean isTokenFound(PasswordResetToken passToken) {
		return passToken != null;
	}

	private boolean isTokenExpired(PasswordResetToken passToken) {
		final Calendar cal = Calendar.getInstance();
		return passToken.getExpiryDate().before(cal.getTime());
	}

}
