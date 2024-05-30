package ua.flowerista.shop.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.dto.AddressDto;
import ua.flowerista.shop.dto.BouqueteSmallDto;
import ua.flowerista.shop.dto.user.*;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.exceptions.UserAlreadyExistException;
import ua.flowerista.shop.mappers.AddressMapper;
import ua.flowerista.shop.mappers.BouqueteMapper;
import ua.flowerista.shop.mappers.UserMapper;
import ua.flowerista.shop.models.*;
import ua.flowerista.shop.repo.BouqueteRepository;
import ua.flowerista.shop.repo.PasswordResetTokenRepository;
import ua.flowerista.shop.repo.UserRepository;
import ua.flowerista.shop.repo.VerificationTokenRepository;

import java.security.Principal;
import java.util.Calendar;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BouqueteRepository bouqueteRepository;

    @Autowired
    private UserMapper userMapper;

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

    public UserProfileDto findByLogin(String login) {
        User user = userRepository.findByEmail(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toProfileDto(user);
    }

    public UserProfileDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.UNAUTHORIZED));

        if (passwordEncoder.matches(credentialsDto.getPassword(), user.getPassword())) {
            return userMapper.toProfileDto(user);
        }

        throw new AppException("Invalid password", HttpStatus.UNAUTHORIZED);
    }

    public User registerNewUserAccount(UserRegistrationBodyDto regDto) {
        if (existsByEmail(regDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: " + regDto.getEmail());
        }
        if (existsByPhoneNumber(regDto.getPhoneNumber())) {
            throw new UserAlreadyExistException(
                    "There is an account with that phone number: " + String.valueOf(regDto.getPhoneNumber()));
        }
        User user = userMapper.toEntity(regDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setAddress(new Address());
        return userRepository.save(user);
    }

//	public Authentication userLogIn(CredentialsDto logDto) {
//		Authentication authentication = authenticationManager
//				.authenticate(new UsernamePasswordAuthenticationToken(logDto.getEmail(), logDto.getPassword()));
//		return authentication;
//	}

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(int phoneNumber) {
        return userRepository.existsByPhoneNumber(String.valueOf(phoneNumber));
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
            userRepository.delete(user);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    public User findUserByEmail(final String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return userRepository.findByEmail(email).get();
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
            userRepository.save(user.get());
            passwordTokenRepository.delete(token);
            return "Password changed";
        }

        return "Something went wrong";
    }

    public UserProfileDto getUserDto(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (user != null) {
            return userMapper.toProfileDto(userRepository.findByEmail(user.getEmail()).get());
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

        userRepository.save(user);
        return "Password changed";
    }

    public void changeAddress(AddressDto address, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Address addressEntity = addressMapper.toEntity(address);
        addressEntity.setId(user.getAddress().getId());
        user.setAddress(addressEntity);
        userRepository.save(user);

    }

    public void changePersonalInfo(UserChangePersonalInfoDto dto, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        userRepository.save(user);
    }

    public Set<BouqueteSmallDto> getWishList(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return user.getWishlist().stream().map(bouquete -> bouqueteMapper.toSmallDto(bouquete)).collect(Collectors.toSet());
    }

    public void addBouqueteToWishList(int id, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Bouquete bouquete = bouqueteRepository.getReferenceById(id);
        user.getWishlist().add(bouquete);
        userRepository.save(user);
    }

    public void deleteBouqueteFromWishList(int id, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Bouquete bouquete = bouqueteRepository.getReferenceById(id);
        user.getWishlist().remove(bouquete);
        userRepository.save(user);
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

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
