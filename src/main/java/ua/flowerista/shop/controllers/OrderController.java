package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.User;
import ua.flowerista.shop.services.JwtService;
import ua.flowerista.shop.services.OrderService;
import ua.flowerista.shop.services.UserService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@CrossOrigin
@Tag(name = "Order controller")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final JwtService jwtService;

    @Operation(summary = "Create new order", description = "Returns bad request if order already exist, and accepted if everything fine")
    @ApiResponses(value = {@ApiResponse(responseCode = "409", description = "If order already exist"),
            @ApiResponse(responseCode = "202", description = "Data was accepted")})
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order, HttpServletRequest request) {
        if (orderService.isOrderExists(order)) {
            return ResponseEntity.status(409).body("Order already exists");
        }
        //TODO: add validation for order
        order.setUserId(extractedUserIdFromAuthHeader(request));
        order = orderService.createOrder(order);
        return ResponseEntity.accepted().body(order);
    }

    @Operation(summary = "Get order by id", description = "Returns order by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "If order not found"),
            @ApiResponse(responseCode = "403", description = "If user is not allowed to see this order"),
            @ApiResponse(responseCode = "200", description = "Return order by id")})
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Integer id, HttpServletRequest request) {
        Optional<Order> order = orderService.getOrder(id);
        if (order.isEmpty()) {
            return ResponseEntity.badRequest().body("Order not found");
        }
        Integer requestUserId = extractedUserIdFromAuthHeader(request);
        if (!order.get().getUserId().equals(requestUserId)) {
            return ResponseEntity.status(403).body("You are not allowed to see this order");
        }
        return ResponseEntity.ok(order.get());
    }

    private Integer extractedUserIdFromAuthHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String jwt;
        String userEmail;
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        User user = userService.findUserByEmail(userEmail);
        return user.getId();
    }
}
