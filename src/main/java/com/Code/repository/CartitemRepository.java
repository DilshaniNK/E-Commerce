package com.Code.repository;

import com.Code.model.Cart;
import com.Code.model.CartItem;
import com.Code.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartitemRepository extends JpaRepository<CartItem,Long> {
    CartItem  findByCartAndProductAndSize(Cart cart, Product product, String size);

}
