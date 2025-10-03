package com.ecom.sb_ecom.controller;

import com.ecom.sb_ecom.payload.OrderDTO;
import com.ecom.sb_ecom.payload.OrderRequestDTO;
import com.ecom.sb_ecom.service.OrderService;
import com.ecom.sb_ecom.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private OrderService orderService;
    private AuthUtil authUtil;

    public OrderController(OrderService orderService, AuthUtil authUtil) {
        this.orderService = orderService;
        this.authUtil = authUtil;
    }

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(
           @PathVariable(name = "paymentMethod") String paymentMethod,
           @RequestBody OrderRequestDTO orderRequestDTO
    ){
        String emailID = authUtil.getLoggerInUser().getEmail();
        OrderDTO orderDTO = orderService.placeOrder(
                emailID,
                paymentMethod,
                orderRequestDTO
        );

        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }
}
