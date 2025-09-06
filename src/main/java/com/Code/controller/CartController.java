package com.Code.controller;

import com.Code.Service.CartItemService;
import com.Code.Service.CartService;
import com.Code.Service.ProductService;
import com.Code.Service.UserService;
import com.Code.model.Cart;
import com.Code.model.CartItem;
import com.Code.model.Product;
import com.Code.model.User;
import com.Code.repository.UserRepository;
import com.Code.request.AddItemRequest;
import com.Code.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Cart> findUserCartHandler(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findUserCart(user);
        return new ResponseEntity<Cart>(cart, HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody
                                                  AddItemRequest req, @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Product product = productService.findProductById(req.getProductId());
        CartItem item = cartService.addCartitem(user,
                product,
                req.getSize(),
                req.getQuantity());
        ApiResponse res = new ApiResponse();
        res.setMessage("item added successfully");
        return new ResponseEntity<>(item, HttpStatus.ACCEPTED);


    }


    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        cartItemService.removeCartItem(user.getId(), cartItemId);
        ApiResponse res = new ApiResponse();
        res.setMessage("item deleted successfully");


        return new  ResponseEntity<>(res, HttpStatus.OK );
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestBody CartItem cartItem, @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        CartItem updatedCartItem = null;
        if(cartItem.getQuantity() > 0 ){
            updatedCartItem = cartItemService.updateCartItem(user.getId(),cartItemId,cartItem);
        }
        return  new ResponseEntity<>(updatedCartItem, HttpStatus.ACCEPTED);
    }

}





