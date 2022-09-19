package com.lec.ecommerse.service;

import com.lec.ecommerse.model.Product;
import com.lec.ecommerse.model.User;
import com.lec.ecommerse.model.WishList;
import com.lec.ecommerse.dto.ProductDTO;
import com.lec.ecommerse.exception.ResourceNotFoundException;
import com.lec.ecommerse.repository.ProductRepository;
import com.lec.ecommerse.repository.UserRepository;
import com.lec.ecommerse.repository.WishListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WishListService {
    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public void createWishList(Long userId, Integer productId) {
        User user = userRepository.getById(userId);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product does not exist !");
        }

        WishList wishList = new WishList(user, optionalProduct.get());
        wishListRepository.save(wishList);
    }

    public List<ProductDTO> listProductInWishList(Long userId) {
        User user = userRepository.getById(userId);
        List<WishList> wishLists = wishListRepository.findAllByUserOrderByCreatedDateDesc(user);

        List<ProductDTO> productDtos = new ArrayList<>();

        for (WishList wishList : wishLists) {
            productDtos.add(productService.getProductDto(wishList.getProduct()));
        }

        return productDtos;
    }

    public void removeItemFromWishList(Long userId, Integer productId) {
        User user = userRepository.getById(userId);

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product does not exist !");
        }
        List<WishList> wishLists = wishListRepository.findAllByUserOrderByCreatedDateDesc(user);

        for (int i = 0; i < wishLists.size(); i++) {
            if (wishLists.get(i).getProduct().getId().equals(productId)) {
                wishListRepository.delete(wishLists.get(i));
            }
        }
    }
}
