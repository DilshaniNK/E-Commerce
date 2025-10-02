package com.Code.Service;

import com.Code.domain.OrderStatus;
import com.Code.model.Address;
import com.Code.model.Cart;
import com.Code.model.Order;
import com.Code.model.User;

import java.util.List;
import java.util.Set;

public interface OrderService {

    Set<Order> createOrder(User user, Address shippingAddress, Cart cart);
    Order findOrderById(Long id) throws Exception;
    List<Order> usersOrderHistory(Long userId);
    List<Order> sellersOrder(Long sellerId);
    Order updateOrderStatus(Long orderId, OrderStatus orderStatus);
    Order cancleOrder(Long orderId,User user) throws Exception;


}
