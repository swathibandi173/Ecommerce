package com.orderservice.application.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.orderservice.application.dto.CartDTO;
import com.orderservice.application.dto.CartDetailsDTO;
import com.orderservice.application.dto.OrderHistoryDTO;
import com.orderservice.application.dto.OrderProductDTO;

public interface OrderService
{
	String orderProducts(OrderProductDTO orderProdDto);
    List<OrderHistoryDTO> getOrderHistory(String userName,String userId);
	ResponseEntity<String> saveCartDetails(String userId, List<CartDTO> cartDto);
	ResponseEntity<List<CartDetailsDTO>> getCartDetails(String userId,String cartId);
}
