package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dto.MarketOrder;
import ca.jrvs.apps.trading.dto.SecurityOrder;
import ca.jrvs.apps.trading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/marketOrder")
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityOrder postMarketOrder(@RequestBody MarketOrder orderDto) {
        try {
            return orderService.postMarketOrder(orderDto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to submit market order", e);
        }
    }
}