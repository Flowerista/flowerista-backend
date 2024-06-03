package ua.flowerista.shop.controllers.adminPanel;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.flowerista.shop.dto.OrderDto;
import ua.flowerista.shop.mappers.OrderMapper;
import ua.flowerista.shop.models.Order;
import ua.flowerista.shop.models.OrderStatus;
import ua.flowerista.shop.services.OrderService;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class OrderAPController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/orders")
    public ModelAndView getOrders(@QuerydslPredicate(root = Order.class)
                                  Predicate predicate,
                                  @RequestParam(name = "page", defaultValue = "0", required = false)
                                  Integer page,
                                  @RequestParam(name = "size", defaultValue = "10", required = false)
                                  Integer size,
                                  Pageable pageable) {
        Page<OrderDto> orders = orderService.getAll(predicate,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).map(orderMapper::toDto);
        return new ModelAndView("admin/orders/ordersList").addObject("orders", orders);
    }

    @GetMapping("/orders/{id}")
    public ModelAndView getById(@PathVariable Integer id) {
        ModelAndView result = new ModelAndView("admin/orders/orderView");
        OrderDto order = orderMapper.toDto(orderService.getById(id).orElseThrow());
        result.addObject("order", order);
        result.addObject("statuses", OrderStatus.values());
        return result;
    }

    @PostMapping("/orders/{id}/status")
    public ModelAndView updateOrderStatus(@PathVariable Integer id, OrderStatus status) {
        orderService.updateStatus(id, status);
        return new ModelAndView("redirect:/api/admin/orders/" + id);
    }

    @PostMapping("/orders/{id}")
    public ModelAndView updateOrder(@PathVariable Integer id, OrderDto order) {
        orderService.update(orderMapper.toEntity(order));
        return new ModelAndView("redirect:/api/admin/orders/" + id);
    }

}
