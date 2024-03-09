package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.models.CompletedOrder;
import ua.flowerista.shop.services.OrderService;
import ua.flowerista.shop.services.PaypalService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/payment")
@CrossOrigin
@Tag(name = "Payment controller")
public class PaymentController {

    private final PaypalService paypalService;
    private final OrderService orderService;

    @PostMapping(value = "/init")
    @Operation(summary = "Create payment endpoint", description = "Returns link to paypal payment page")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Return status creating payment, payId and redirectUrl"),
                    @ApiResponse(responseCode = "400", description = "If order already payed")})
    public ResponseEntity<?> createPayment(@RequestParam("orderId") Integer orderId) {
        if (orderService.isOrderPayed(orderId)) {
            return ResponseEntity.badRequest().body("Order already payed or not found");
        }
        if (orderService.isOrderWaitingForPayment(orderId)) {
            return  ResponseEntity.ok(paypalService.getPaymentForOrder(orderId));
        }
        return ResponseEntity.accepted().body(paypalService.createPayment(orderId));
    }

    @Operation(summary = "Complete payment endpoint", description = "Returns payment status and payId")
    @PostMapping(value = "/capture")
    public CompletedOrder completePayment(@RequestParam("token") String token) {
        return paypalService.completePayment(token);
    }
}
