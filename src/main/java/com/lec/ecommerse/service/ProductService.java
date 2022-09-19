package com.lec.ecommerse.service;

import com.lec.ecommerse.model.Category;
import com.lec.ecommerse.model.Product;
import com.lec.ecommerse.dto.ProductDTO;
import com.lec.ecommerse.exception.ResourceNotFoundException;
import com.lec.ecommerse.repository.CategoryRepository;
import com.lec.ecommerse.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    public void createProduct(ProductDTO productDTO) {
        Optional<Category> optionalCategory = categoryRepository.findById(productDTO.getCategoryId());
        if (!optionalCategory.isPresent()) {
            throw new ResourceNotFoundException("Category does not exist !");
        }
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setImageUrl(productDTO.getImageUrl());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setCategory(optionalCategory.get());
        productRepository.save(product);
    }

    public List<ProductDTO> listProduct() {
        List<Product> productList = productRepository.findAll();

        List<ProductDTO> productDtos = new ArrayList<>();

        for (Product product : productList) {
            productDtos.add(getProductDto(product));
        }
        return productDtos;
    }

    public ProductDTO getProductDto(Product product) {
        ProductDTO productDto = new ProductDTO();
        productDto.setDescription(product.getDescription());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setName(product.getName());
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setPrice(product.getPrice());
        productDto.setId(product.getId());
        return productDto;
    }

    public void editProduct(ProductDTO productDTO, Integer id) {

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()){
            throw new ResourceNotFoundException("Product does not exist !");
        }
        Product product = optionalProduct.get();
        product.setName(productDTO.getName());
        product.setImageUrl(productDTO.getImageUrl());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setCategory(optionalProduct.get().getCategory());
        productRepository.save(product);
    }

    public void removeById(Integer id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()){
            throw new ResourceNotFoundException("Product does not exist !");
        }
        productRepository.deleteById(id);
    }
}
