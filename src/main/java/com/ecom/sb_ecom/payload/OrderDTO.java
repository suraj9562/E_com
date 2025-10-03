package com.ecom.sb_ecom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItemDTOList;
    private LocalDateTime orderDate;
    private PaymentDTO payment;
    private Double totalAmount;
    private Long addressId;
}
