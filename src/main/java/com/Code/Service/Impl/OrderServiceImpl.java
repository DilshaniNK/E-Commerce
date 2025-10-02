package com.Code.Service.Impl;

import com.Code.Service.OrderService;
import com.Code.domain.OrderStatus;
import com.Code.domain.PaymentStatus;
import com.Code.model.*;
import com.Code.repository.AddressRepository;
import com.Code.repository.OrderItemRepository;
import com.Code.repository.OrderRepository;
import com.Code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;


    @Override
    public Set<Order> createOrder(User user, Address shippingAddress, Cart cart) {
        if(!user.getAddresses().contains(shippingAddress)){
            user.getAddresses().add(shippingAddress);
        }
        Address address = addressRepository.save(shippingAddress);

        Map<Long,List<CartItem>> itemsBySeller = cart.getCartItems().stream()
                .collect(Collectors.groupingBy(item->item.getProduct()
                        .getSeller().getId()));

        Set<Order> orders = new HashSet<>();
        for(Map.Entry<Long,List<CartItem>> entry:itemsBySeller.entrySet()){
            Long sellerId = entry.getKey();
            List<CartItem> cartItems = entry.getValue();


            int totalOrderPrice = cartItems.stream().mapToInt(
                    CartItem::getSellingprice
            ).sum();
            int totalItems = cartItems.stream().mapToInt(
                    CartItem::getQuantity
            ).sum();
            Order createdOrder = new Order();
            createdOrder.setUser(user);
            createdOrder.setSellerId(sellerId);
            createdOrder.setTotalMrpPrice(totalOrderPrice);
            createdOrder.setTotalSellingPrice(totalOrderPrice);
            createdOrder.setTotalItems(totalItems);
            createdOrder.setShippingAddress(address);
            createdOrder.setOrderStatus(OrderStatus.PENDING);
            createdOrder.getPaymentDetails().setStatus(PaymentStatus.PENDING);

            Order savedOrder = orderRepository.save(createdOrder);
            orders.add(savedOrder);

            List<OrderItem> orderItems = new ArrayList<>();
            for(CartItem item:cartItems){
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setMrpPrice(item.getMrpPrice());
                orderItem.setProduct(item.getProduct());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setSize(item.getSize());
                orderItem.setUserId(item.getUserId());
                orderItem.setSellingPrice(item.getSellingprice());

                savedOrder.getOrderItems().add(orderItem);

                OrderItem savedOrderItem = orderItemRepository.save(orderItem);
                orderItems.add(savedOrderItem);

            }
        }
        return orders;
    }

    @Override
    public Order findOrderById(Long id) throws Exception {

        return orderRepository.findById(id).orElseThrow(()->
                new Exception("Order Not found"));
    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> sellersOrder(Long sellerId) {
        return List.of();
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        return null;
    }

    @Override
    public Order cancleOrder(Long orderId, User user) {
        return null;
    }
}
