package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.AddressDto;
import ua.flowerista.shop.dto.user.PersonalInfoDto;
import ua.flowerista.shop.dto.user.UpdatePasswordDto;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.mappers.BouquetMapper;
import ua.flowerista.shop.mappers.UserMapper;
import ua.flowerista.shop.models.textContent.Languages;
import ua.flowerista.shop.models.user.User;
import ua.flowerista.shop.services.UserService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@CrossOrigin
@Tag(name = "USER controller", description = "Operations that auth`d user can do")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final BouquetMapper bouquetMapper;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile dto",
            description = "Returns user profile dto")
    public ResponseEntity<?> profile(Principal principal) {
        Integer id = getIdFromPrincipal(principal);
        return ResponseEntity.ok(userService.findById(id)
                .map(userMapper::toProfileDto)
                .orElseThrow(() -> {
                    logger.error("Authenticated user not found in db: {}!", principal.getName());
                    return new AppException("Authenticated user not found in db!", HttpStatus.INTERNAL_SERVER_ERROR);
                }));
    }

    @Operation(summary = "Change password endpoint",
            description = "Changing authenticated users passwords")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "If passwords is not valid or not equals"),
            @ApiResponse(responseCode = "202", description = "If password was changed")})
    @PatchMapping("/changePassword")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordDto request,
                                            @NotNull Principal principal) {
        userService.updatePassword(request, principal);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Change address endpoint",
            description = "Changing authenticated users addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "If address was updated")})
    @PatchMapping("/changeAddress")
    public ResponseEntity<?> updateAddress(@RequestBody @Valid AddressDto address,
                                           @NotNull Principal principal) {
        userService.changeAddress(address, principal);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/changePersonalInfo")
    @Operation(summary = "Change user personal info endpoint",
            description = "Changing authenticated users personal info`s")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "When personal info was updated")})
    public ResponseEntity<?> updatePersonalInfo(@RequestBody @Valid PersonalInfoDto dto,
                                                Principal principal) {
        userService.changePersonalInfo(dto, principal);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/wishlist")
    @Operation(summary = "Get users wishlist",
            description = "Returns set of bouquets that user have in wishlist")
    public ResponseEntity<?> getWishList(Principal principal, @RequestParam(defaultValue = "en") Languages lang ) {
        Integer id = getIdFromPrincipal(principal);
        return ResponseEntity.ok(userService.getWishList(id).stream()
                .map(bouquet -> bouquetMapper.toDto(bouquet, lang))
                .toList());
    }

    @PostMapping("/wishlist")
    @Operation(summary = "Add bouquet to wishlist",
            description = "Accepting bouquet id in body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "When bouquet was added to wishlist")})
    public ResponseEntity<?> addBouquetToWishList(@RequestBody Map<String, Integer> map, Principal principal) {
        userService.addBouquetToWishList(map.get("id"), principal);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/wishlist")
    @Operation(summary = "Remove bouquet from wishlist", description = "Accepting bouquet id in body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Bouquet was removed from wishlist")})
    public ResponseEntity<?> deleteBouquetFromWishList(@RequestBody Map<String, Integer> map, Principal principal) {
        userService.removeBouquetFromWishList(map.get("id"), principal);
        return ResponseEntity.accepted().build();
    }

    private static Integer getIdFromPrincipal(Principal principal) {
        if (((UsernamePasswordAuthenticationToken) principal).getPrincipal() instanceof User) {
            return ((User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();
        } else {
            return Integer.valueOf(principal.getName());
        }
    }
}
