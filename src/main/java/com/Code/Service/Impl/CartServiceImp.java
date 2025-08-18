package com.Code.Service.Impl;

import com.Code.Service.CartService;
import com.Code.model.Cart;
import com.Code.model.CartItem;
import com.Code.model.Product;
import com.Code.model.User;
import com.Code.repository.CartRepository;
import com.Code.repository.CartitemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImp implements CartService {

    private final CartRepository cartRepository;
    private final CartitemRepository cartitemRepository;

    @Override
    public CartItem addCartitem(User user, Product product, String size, int quantity) {
        Cart cart = findUserCart(user);
        CartItem isPresent = cartitemRepository.findByCartAndProductAndSize(cart, product, size);
        if(isPresent == null) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUserId(user.getId());
            cartItem.setSize(size);
            int totalPrice = quantity*product.getSellingPrice();
            cartItem.setSellingprice(totalPrice);
            cart.getCartItems().add(cartItem);
            cartItem.setCart(cart);

            return cartitemRepository.save(cartItem);
        }
        return isPresent;
    }

    @Override
    public Cart findUserCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId());

        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItems = 0;

        for(CartItem cartItem : cart.getCartItems()) {
            totalPrice += cartItem.getMrpPrice();
            totalDiscountedPrice += cartItem.getSellingprice();
            totalItems += cartItem.getQuantity();

        }


        cart.setTotalMrpPrice(totalPrice);
        cart.setTotalSellingPrice(totalDiscountedPrice);
        cart.setTotalItem(totalItems);
        cart.setDiscount(calculateDiscountPercentage(totalPrice, totalDiscountedPrice));
        return null;
    }

    private int calculateDiscountPercentage(int mrpPrice, int sellingPrice) {
        if(mrpPrice<=0){
            throw new IllegalArgumentException("MrpPrice must be greater than 0");
        }
        double discount = mrpPrice - sellingPrice;
        double discountPercentage = (discount / mrpPrice) * 100;
        return (int)discountPercentage;

    }
}
