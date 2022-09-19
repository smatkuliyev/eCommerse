package com.lec.ecommerse.controller;

import com.lec.ecommerse.dto.ProductDTO;
import com.lec.ecommerse.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> createProduct(@RequestBody ProductDTO productDTO) {
        productService.createProduct(productDTO);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Product has been added successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> listProduct() {
        List<ProductDTO> productList = productService.listProduct();

        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDTO) {
        productService.editProduct(productDTO, id);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Product has been updated successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteProduct(@PathVariable Integer id) {

        productService.removeById(id);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Product has been updated successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
