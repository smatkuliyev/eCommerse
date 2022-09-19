package com.lec.ecommerse.repository;

import com.lec.ecommerse.model.Cart;
import com.lec.ecommerse.model.Product;
import com.lec.ecommerse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    Boolean existsByProduct(Product product);

    List<Cart> findAllByUser(User user);
}
