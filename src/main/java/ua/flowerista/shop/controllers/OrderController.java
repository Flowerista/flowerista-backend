package ua.flowerista.shop.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ua.flowerista.shop.dto.order.OrderDto;
import ua.flowerista.shop.mappers.OrderMapper;
import ua.flowerista.shop.models.order.Order;
import ua.flowerista.shop.models.user.User;
import ua.flowerista.shop.services.OrderService;
import ua.flowerista.shop.services.validators.OrderValidator;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@CrossOrigin
@Tag(name = "Order controller")
public class OrderController {
    private final OrderService orderService;
    private final OrderValidator orderValidator;
    private final OrderMapper orderMapper;

    @Operation(summary = "Create new order",
            description = "Returns bad request if order already exist, and accepted if everything fine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "If order already exist"),
            @ApiResponse(responseCode = "202", description = "Data was accepted")})
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto order, Principal principal) {
        order.setUserId(getIdFromPrincipal(principal));
        List<String> errors = orderValidator.validateOrder(order);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors.toString());
        }
        if (orderService.isExists(order.getId())) {
            return ResponseEntity.status(409).body("Order already exists");
        }
        Order savedOrder = orderService.create(orderMapper.toEntity(order));
        return ResponseEntity.accepted().body(orderMapper.toDto(savedOrder));
    }

    @Operation(summary = "Get order by id", description = "Returns order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "If order not found"),
            @ApiResponse(responseCode = "403", description = "If user is not allowed to see this order"),
            @ApiResponse(responseCode = "200", description = "Return order by id")})
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Integer id, Principal principal) {
        Optional<Order> order = orderService.getById(id);
        if (order.isEmpty()) {
            return ResponseEntity.badRequest().body("Order not found");
        }
        Integer principalUserId = getIdFromPrincipal(principal);
        if (!order.get().getUser().getId().equals(principalUserId)) {
            return ResponseEntity.status(403).body("You are not allowed to see this order");
        }
        return ResponseEntity.ok(orderMapper.toDto(order.get()));
    }

    @Operation(summary = "Update order by id", description = "Update order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "If user is not allowed to update this order"),
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "If order is not valid"),
            @ApiResponse(responseCode = "202", description = "Data was accepted and order was created")})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer id, @RequestBody OrderDto order, Principal principal) {
        // validate order
        List<String> errors = orderValidator.validateOrder(order);
        if (!id.equals(order.getId())) {
            errors.add("Id in path and in body are not equal");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors.toString());
        }
        Order result;
        // if order not exists - create new order
        if (!orderService.isExists(id)) {
            order.setUserId(getIdFromPrincipal(principal));
            result = orderService.create(orderMapper.toEntity(order));
            return ResponseEntity.accepted().body(orderMapper.toDto(result));
        } else {
            // if order exists - check if user is allowed to update this order
            Integer principalUserId = getIdFromPrincipal(principal);
            Integer orderUserId = orderService.getUserId(id);
            if (!orderUserId.equals(principalUserId)) {
                return ResponseEntity.status(403).body("You are not allowed to update this order");
            }
        }
        //update order
        Order updatedOrder = orderService.update(orderMapper.toEntity(order));
        return ResponseEntity.accepted().body(orderMapper.toDto(updatedOrder));
    }

    @Operation(summary = "Get orders history", description = "The history of all the user's orders is returned.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Return orders history")})
    @GetMapping("/history")
    public ResponseEntity<?> getAllOrdersByUser(Principal principal) {
        List<Order> orders = orderService.getByUserId(getIdFromPrincipal(principal));
        return ResponseEntity.ok(orderMapper.toDto(orders));
    }

    private static Integer getIdFromPrincipal(Principal principal) {
        if (((UsernamePasswordAuthenticationToken) principal).getPrincipal() instanceof User){
            return ((User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();
        } else {
            return Integer.valueOf(principal.getName());
        }
    }
}
