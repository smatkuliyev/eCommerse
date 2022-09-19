package com.lec.ecommerse.dto;

import com.lec.ecommerse.model.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class CartDTO {

    private Integer id;

    @NotNull
    private Integer productId;

    @NotNull
    private Integer quantity;

    private Product product;

    public CartDTO(Integer id,Integer productId, Product product, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.product = product;
        this.quantity = quantity;
    }
}
