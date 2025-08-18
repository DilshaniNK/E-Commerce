package com.Code.Service;

import com.Code.model.Cart;
import com.Code.model.CartItem;
import com.Code.model.Product;
import com.Code.model.User;

public interface CartService {

    public CartItem addCartitem(
            User user,
            Product product,
            String size,
            int quantity
    );
    public Cart findUserCart(User user);

}
