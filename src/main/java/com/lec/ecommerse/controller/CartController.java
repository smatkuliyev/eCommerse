package com.lec.ecommerse.controller;

import com.lec.ecommerse.dto.CartDTO;
import com.lec.ecommerse.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
    public ResponseEntity<Map<String, Boolean>> listProductsOfCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");

        Map<String, Boolean> map = new HashMap<>();
        map.put("Product has been added to Cart successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
