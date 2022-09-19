package com.lec.ecommerse.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Setter
@Getter
public class CartDTO {

    private Integer id;

    @NotNull
    private Integer productId;

    @NotNull
    private Integer quantity;
}
