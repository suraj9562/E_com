package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.exceptions.ApiException;
import com.ecom.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecom.sb_ecom.model.Cart;
import com.ecom.sb_ecom.model.CartItem;
import com.ecom.sb_ecom.model.Product;
import com.ecom.sb_ecom.model.User;
import com.ecom.sb_ecom.payload.CartDTO;
import com.ecom.sb_ecom.payload.CartResponse;
import com.ecom.sb_ecom.payload.ProductDTO;
import com.ecom.sb_ecom.repository.CartItemRepository;
import com.ecom.sb_ecom.repository.CartRepository;
import com.ecom.sb_ecom.repository.ProductRepository;
import com.ecom.sb_ecom.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product", "productId", productId)
        );

        if (product.getQuantity() < quantity) {
            throw new ApiException("Product quantity less than cart quantity");
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart.getCartId(), productId).orElse(null);

        if(cartItem != null) {
            throw new ApiException("Product already exists in Cart");
        }

        cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());
        cartItem.setQuantity(quantity);
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() + (savedCartItem.getPrice() * savedCartItem.getQuantity()));
        Cart savedCart = cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = savedCart.getCartItems();
        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        cartDTO.setCartItems(productStream.toList());
        return cartDTO;
    }

    @Override
    public CartResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Cart> page = cartRepository.findAll(pageable);
        List<Cart> carts = page.getContent();

        if(carts.isEmpty()) {
            throw new ApiException("No carts found");
        }

        List<CartDTO> cartDtos = carts.stream().map(
                (cart) -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> productDTOS = cart.getCartItems().stream().map(
                            (item) -> {
                                ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
                                dto.setQuantity(item.getQuantity());

                                return dto;
                            }
                    ).toList();

                    cartDTO.setCartItems(productDTOS);
                    return cartDTO;
                }
        ).toList();

        CartResponse cartResponse =  new CartResponse();
        cartResponse.setCartDTOList(cartDtos);
        cartResponse.setPageNumber(page.getNumber());
        cartResponse.setPageSize(page.getSize());
        cartResponse.setTotalPages(page.getTotalPages());
        cartResponse.setTotalElements(page.getTotalElements());
        cartResponse.setLastPage(page.isLast());

        return cartResponse;
    }

    @Override
    public CartDTO getCart() {
        User user = authUtil.getLoggerInUser();
        Cart cart = cartRepository.findByUserId(user.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("Cart", "userId", user.getUserId())
        );

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(
                (item) -> {
                    ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    dto.setQuantity(item.getQuantity());

                    return dto;
                }
        ).toList();

        cartDTO.setCartItems(productDTOS);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantity(Long productId, Integer quantity) {
        Long userId = authUtil.getLoggerInUser().getUserId();
        Cart cart = cartRepository.findByUserId(userId).
                orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new ApiException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart.getCartId(), productId).orElseThrow(
                () -> new ApiException("Product " + product.getProductName() + " not available in the cart!!!")
        );

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        // Validation to prevent negative quantities
        if (newQuantity < 0) {
            throw new ApiException("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0){
            deleteProductFromCart(cart.getCartId(), productId);
        } else {
            cartItem.setPrice(product.getSpecialPrice());
            cartItem.setQuantity(newQuantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        List<ProductDTO> products = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        }).toList();

        cartDTO.setCartItems(products);

        return cartDTO;
    }


    private Cart createCart() {
        User user = authUtil.getLoggerInUser();
        Cart userCart  = cartRepository.findByUserId(user.getUserId()).orElse(null);
        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cartId, productId).orElseThrow(
                () -> new ApiException("Product " + productId + " not available in the cart!!!")
        );

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductAndCart(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

    @Transactional
    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart.getCartId(), productId).orElseThrow(
                () -> new ApiException("Product " + productId + " not available in the cart!!!")
        );

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getPrice() * cartItem.getQuantity());

        cartItem.setPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }
}

