package com.lec.ecommerse.service;

import com.lec.ecommerse.dto.CartDTO;
import com.lec.ecommerse.exception.BadRequestException;
import com.lec.ecommerse.exception.ResourceNotFoundException;
import com.lec.ecommerse.model.Cart;
import com.lec.ecommerse.model.Product;
import com.lec.ecommerse.model.User;
import com.lec.ecommerse.repository.CartRepository;
import com.lec.ecommerse.repository.ProductRepository;
import com.lec.ecommerse.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
        if (!existsByProduct) {
            Cart cart = new Cart();
            cart.setProduct(optionalProduct.get());
            cart.setUser(user);
            cart.setQuantity(cartDTO.getQuantity());
            cart.setCreatedDate(new Date());
            cartRepository.save(cart);
        }
    }

    public Map<List<CartDTO>, Double> listCart(Long userId) {
        User user = userRepository.getById(userId);
        List<Cart> cartList = cartRepository.findAllByUser(user);

        List<CartDTO> cartDTOs = new ArrayList<>();
        double totalCost = 0;

        for (Cart cart : cartList) {
            CartDTO cartDto = new CartDTO(cart.getId(), cart.getProduct().getId(), cart.getProduct(), cart.getQuantity());
            totalCost += cartDto.getQuantity() * cart.getProduct().getPrice();
            cartDTOs.add(cartDto);
        }

        Map<List<CartDTO>, Double> map = new HashMap<>();
        map.put(cartDTOs, totalCost);
        return map;
    }

    public void removeById(Long userId, Integer id) {
        User user = userRepository.getById(userId);
        Optional<Cart> optionalCart = cartRepository.findById(id);
        if (optionalCart.isEmpty()) {
            throw new ResourceNotFoundException("Item does not exist in Cart");
        }
        Cart cart = optionalCart.get();
        if (cart.getUser() != user) {
            throw new BadRequestException("Item does not belong to user: " + user.getFirstName());
        }

        cartRepository.delete(cart);
    }
}
