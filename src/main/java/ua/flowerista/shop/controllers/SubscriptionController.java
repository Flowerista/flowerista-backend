package ua.flowerista.shop.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.SubscriptionDto;
import ua.flowerista.shop.services.SubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
@CrossOrigin
@Tag(name = "Subscription controller")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Add email to subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "If email already exists"),
            @ApiResponse(responseCode = "202", description = "If email was accepted")})
    public void subscribe(@RequestBody @Valid SubscriptionDto request) {
        subscriptionService.sub(request);
    }
}
