package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.models.CompletedOrder;
import ua.flowerista.shop.models.PaymentOrder;
import ua.flowerista.shop.services.PaypalService;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/payment")
@CrossOrigin
@Tag(name = "Payment controller")
public class PaymentController {

    private final PaypalService paypalService;

    @PostMapping(value = "/init")
    @Operation(summary = "Create payment endpoint", description = "Returns link to paypal payment page")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Return status creating payment, payId and redirectUrl")})
    public PaymentOrder createPayment(
            @RequestParam("sum") BigDecimal sum, @RequestParam("currency") String currency, @RequestParam("orderId") Integer orderId) {
        return paypalService.createPayment(sum, currency, orderId);
    }

    @Operation(summary = "Complete payment endpoint", description = "Returns payment status and payId")
    @PostMapping(value = "/capture")
    public CompletedOrder completePayment(@RequestParam("token") String token) {
        return paypalService.completePayment(token);
    }
}
