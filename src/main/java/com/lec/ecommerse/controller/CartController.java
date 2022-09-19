package com.lec.ecommerse.controller;

import com.lec.ecommerse.dto.CartDTO;
import com.lec.ecommerse.model.Category;
import com.lec.ecommerse.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> addtoCart(HttpServletRequest request, @RequestBody CartDTO cartDTO) {
        Long userId = (Long) request.getAttribute("id");

        cartService.addToCart(userId, cartDTO);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Product has been added to Cart successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<List<CartDTO>, Double>> listProductsOfCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");

        Map<List<CartDTO>, Double> map1 = cartService.listCart(userId);

        return new ResponseEntity<>(map1, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteProductsFromCart(HttpServletRequest request, @PathVariable Integer id) {
        Long userId = (Long) request.getAttribute("id");
        cartService.removeById(userId, id);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Product has been removed from Cart successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
