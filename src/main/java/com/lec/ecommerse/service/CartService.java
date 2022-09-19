package com.lec.ecommerse.service;

import com.lec.ecommerse.dto.CartDTO;
import com.lec.ecommerse.exception.ResourceNotFoundException;
import com.lec.ecommerse.model.Cart;
import com.lec.ecommerse.model.Product;
import com.lec.ecommerse.model.User;
import com.lec.ecommerse.repository.CartRepository;
import com.lec.ecommerse.repository.ProductRepository;
import com.lec.ecommerse.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addToCart(Long userId, CartDTO cartDTO) {
        User user = userRepository.getById(userId);
        Optional<Product> optionalProduct = productRepository.findById(cartDTO.getProductId());
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product does not exist !");
        }

        List<Cart> cartList = cartRepository.findAll();
        for (Cart cart1 : cartList) {
            if (cart1.getProduct().getId().equals(cartDTO.getProductId())) {
                cart1.setQuantity(cart1.getQuantity() + cartDTO.getQuantity());
                cartRepository.save(cart1);
            }
        }
        Boolean existsByProduct = cartRepository.existsByProduct(optionalProduct.get());
        if (!existsByProduct){
            Cart cart = new Cart();
            cart.setProduct(optionalProduct.get());
            cart.setUser(user);
            cart.setQuantity(cartDTO.getQuantity());
            cart.setCreatedDate(new Date());
            cartRepository.save(cart);
        }



    }
}
