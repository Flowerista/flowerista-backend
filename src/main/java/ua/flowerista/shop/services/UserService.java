package ua.flowerista.shop.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.flowerista.shop.dto.AddressDto;
import ua.flowerista.shop.dto.user.*;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.mappers.AddressMapper;
import ua.flowerista.shop.mappers.BouquetMapper;
import ua.flowerista.shop.mappers.UserMapper;
import ua.flowerista.shop.models.Address;
import ua.flowerista.shop.models.Bouquet;
import ua.flowerista.shop.models.VerificationToken;
import ua.flowerista.shop.models.user.Role;
import ua.flowerista.shop.models.user.User;
import ua.flowerista.shop.repositories.BouquetRepository;
import ua.flowerista.shop.repositories.UserRepository;

import java.security.Principal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final RedisService redisService;
    private final MailService mailService;

    private final UserRepository userRepository;
    private final BouquetRepository bouquetRepository;

    private final UserMapper userMapper;
    private final AddressMapper addressMapper;
    private final BouquetMapper bouquetMapper;

    private final PasswordEncoder passwordEncoder;

    private static final long PASSWORD_TOKEN_EXPIRATION = 60 * 24L;
    private static final long REGISTRATION_TOKEN_EXPIRATION = 60 * 24L;

    public Optional<User> findByLogin(String login) {
        return userRepository.findByEmail(login);
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found by id: {}", id);
                    return new AppException("User not found", HttpStatus.NOT_FOUND);
                });
    }

    public Optional<User> login(CredentialsDto credentialsDto) {
        User user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.FORBIDDEN));
        if (passwordEncoder.matches(credentialsDto.getPassword(), user.getPassword())) {
            return Optional.of(user);
        }
        throw new AppException("Invalid password", HttpStatus.FORBIDDEN);
    }

    @Transactional
    public User registerNewUserAccount(SignUpDto regDto) {
        if (existsByEmail(regDto.getEmail())) {
            throw new AppException(
                    "There is an account with that email address: " + regDto.getEmail(),
                    HttpStatus.BAD_REQUEST);
        }
        if (existsByPhoneNumber(regDto.getPhoneNumber())) {
            throw new AppException(
                    "There is an account with that phone number: " + regDto.getPhoneNumber(),
                    HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.toEntity(regDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setAddress(new Address());
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(Integer phoneNumber) {
        return userRepository.existsByPhoneNumber(String.valueOf(phoneNumber));
    }

    public void sendRegistrationVerificationEmail(User user, Locale locale, String appUrl) {
        VerificationToken token = createVerificationTokenForUser(user, REGISTRATION_TOKEN_EXPIRATION);
        mailService.sendRegistrationVerificationEmail(user, token.getId(), appUrl, locale);
    }

    public void sendPasswordResetEmail(User user) {
        VerificationToken token = createVerificationTokenForUser(user, PASSWORD_TOKEN_EXPIRATION);
        mailService.sendResetPasswordEmail(user, token.getId());
    }

    @Transactional
    public void processRegistrationToken(String token) {
        VerificationToken savedToken = redisService.getToken(token);
        if (savedToken == null) {
            throw new AppException("Token is expired or invalid", HttpStatus.BAD_REQUEST);
        }
        userRepository.updateEnabledByEmail(savedToken.getUserLogin());
    }

    @Transactional
    public void resetPassword(ResetPasswordDto dto) {
        if (!dto.getPassword().equals(dto.getPasswordRepeated())) {
            throw new AppException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }
        VerificationToken savedToken = redisService.getToken(dto.getToken());
        if (savedToken == null) {
            throw new AppException("Token is expired or invalid", HttpStatus.BAD_REQUEST);
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        userRepository.updatePasswordByEmail(savedToken.getUserLogin(), encodedPassword);
    }

    @Transactional
    public void updatePassword(UpdatePasswordDto request, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException("Wrong password", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void changeAddress(AddressDto address, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Address addressEntity = addressMapper.toEntity(address);
        addressEntity.setId(user.getAddress().getId());
        user.setAddress(addressEntity);
        userRepository.save(user);
    }

    @Transactional
    public void changePersonalInfo(PersonalInfoDto dto, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        userRepository.save(user);
    }

    public Set<Bouquet> getWishList(Integer userId) {
        return new HashSet<>(userRepository.findById(userId)
                .map(User::getWishlist)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND)));
    }

    @Transactional
    public void addBouquetToWishList(Integer id, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        User user = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new AppException("Logged User not found. {} " + loggedUser,
                        HttpStatus.INTERNAL_SERVER_ERROR));
        Bouquet bouquet = bouquetRepository.getReferenceById(id);
        user.getWishlist().add(bouquet);
        userRepository.save(user);
    }

    @Transactional
    public void removeBouquetFromWishList(int id, Principal principal) {
        User loggedUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        User user = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new AppException("Logged User not found. {} " + loggedUser,
                        HttpStatus.INTERNAL_SERVER_ERROR));
        Bouquet bouquet = bouquetRepository.getReferenceById(id);
        user.getWishlist().remove(bouquet);
        userRepository.save(user);
    }

    private VerificationToken createVerificationTokenForUser(User user, Long expirationTime) {
        VerificationToken token = new VerificationToken(user.getEmail());
        redisService.saveToken(token.getId(), token, expirationTime);
        return token;
    }

}
