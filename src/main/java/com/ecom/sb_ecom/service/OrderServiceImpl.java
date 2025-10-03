package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.exceptions.ApiException;
import com.ecom.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecom.sb_ecom.model.*;
import com.ecom.sb_ecom.payload.OrderDTO;
import com.ecom.sb_ecom.payload.OrderItemDTO;
import com.ecom.sb_ecom.payload.OrderRequestDTO;
import com.ecom.sb_ecom.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressesRepository addressesRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            AddressesRepository addressesRepository,
            PaymentRepository paymentRepository,
            ModelMapper modelMapper,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.addressesRepository = addressesRepository;
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional
    @Override
    public OrderDTO placeOrder(String emailID, String paymentMethod, OrderRequestDTO orderRequestDTO){
        // getting user cart
        Cart cart = cartRepository.findCartByEmailId(emailID).orElseThrow(
                () -> new ResourceNotFoundException("Cart", "email", emailID)
        );

        // fetch address object
        Address address = addressesRepository.findById(orderRequestDTO.getAddressId()).orElseThrow(
                () -> new ResourceNotFoundException("Address", "id", orderRequestDTO.getAddressId())
        );

        // Payment object
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        payment.setPgName(orderRequestDTO.getPgName());
        payment.setPgPaymentId(orderRequestDTO.getPgPaymentId());
        payment.setPgStatus(orderRequestDTO.getPgStatus());
        payment.setPgResponseMessage(orderRequestDTO.getPgResponseMessage());

        // create new order with payment info
        Order order = new Order();
        order.setEmail(emailID);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");

        payment.setOrder(order);
        Payment savedPayment = paymentRepository.save(payment);

        order.setPayment(savedPayment);
        Order savedOrder = orderRepository.save(order);

        // get items from the cart into order items
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new ApiException("Cart is empty");
        }

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setOrderedProductPrice(cartItem.getPrice());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            return orderItemRepository.save(orderItem);
        }).toList();

        // update product stock
        cartItems.forEach(cartItem -> {
            int quantity = cartItem.getQuantity();
            Product product = cartItem.getProduct();

            if(product.getQuantity() - quantity < 0){
                throw new ApiException("Product Quantity Not Enough");
            }

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            // clear the cart
            cartService.deleteProductFromCart(cart.getCartId(), product.getProductId());
        });

        // send back the order summary
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderDTO.setOrderItemDTOList(
                orderItems.stream().map(
                        orderItem -> {
                            return modelMapper.map(orderItem, OrderItemDTO.class);
                        }
                ).toList()
        );
        orderDTO.setAddressId(address.getAddressId());

        return orderDTO;
    }
}
