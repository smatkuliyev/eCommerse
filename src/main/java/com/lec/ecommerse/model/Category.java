package com.lec.ecommerse.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "category_name")
    private String categoryName;

    @NotNull
    private String description;

    @NotNull
    @Column(name = "image_url")
    private String imageUrl;
}
