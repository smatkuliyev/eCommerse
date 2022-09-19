package com.lec.ecommerse.controller;

import com.lec.ecommerse.dto.ProductDTO;
import com.lec.ecommerse.service.WishListService;
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
@RequestMapping("/wishlist")
@AllArgsConstructor
public class WishListController {

    private final WishListService wishListService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> addToWishList(HttpServletRequest request, @RequestParam("productId") Integer productId){
        Long userId = (Long) request.getAttribute("id");

        wishListService.createWishList(userId, productId);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Product has been added to wishlist successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> listWishList(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("id");

        List<ProductDTO> productList = wishListService.listProductInWishList(userId);

        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> removeFromWishList(HttpServletRequest request, @PathVariable Integer productId){
        Long userId = (Long) request.getAttribute("id");
        wishListService.removeItemFromWishList(userId, productId);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Product has been removed from wishlist successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
