package ua.flowerista.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.flowerista.shop.dto.OrderDto;
import ua.flowerista.shop.mappers.OrderMapper;
import ua.flowerista.shop.models.OrderStatus;
import ua.flowerista.shop.services.OrderService;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPanelController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/orders")
    public ModelAndView getOrders() {
        List<OrderDto> orders = orderService.getAllOrders().stream()
                .map(orderMapper::toDto)
                .toList();
        return new ModelAndView("admin/orders/ordersList").addObject("orders", orders);
    }

    @GetMapping("/orders/{id}")
    public ModelAndView getOrder(@PathVariable Integer id) {
        OrderDto order = orderMapper.toDto(orderService.getOrder(id).orElseThrow());
        return new ModelAndView("admin/orders/orderView").addObject("order", order);
    }

    @PostMapping("/orders/{id}/status")
    public ModelAndView updateOrderStatus(@PathVariable Integer id, OrderStatus status) {
        orderService.updateStatus(id, status);
        return new ModelAndView("redirect:/api/admin/orders/" + id);
    }

    @PostMapping("/orders/{id}")
    public ModelAndView updateOrder(@PathVariable Integer id, OrderDto order) {
        orderService.updateOrder(id, orderMapper.toEntity(order));
        return new ModelAndView("redirect:/api/admin/orders/" + id);
    }
}
