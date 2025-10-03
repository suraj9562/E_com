package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.payload.OrderDTO;
import com.ecom.sb_ecom.payload.OrderRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {

    @Transactional
    OrderDTO placeOrder(String emailID, String paymentMethod, OrderRequestDTO orderRequestDTO);
}
