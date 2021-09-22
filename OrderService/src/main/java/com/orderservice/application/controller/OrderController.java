package com.orderservice.application.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderservice.application.dto.CartDTO;
import com.orderservice.application.dto.CartDetailsDTO;
import com.orderservice.application.dto.OrderHistoryDTO;
import com.orderservice.application.dto.OrderProductDTO;
import com.orderservice.application.exception.InSufficientBalanceException;
import com.orderservice.application.exception.ResourceNotFoundException;
import com.orderservice.application.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orderservice")
@Slf4j
@EnableTransactionManagement
public class OrderController 
{
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	OrderService orderService;
	
	@PostMapping("/addCart/{userId}")
	public ResponseEntity<String> saveCartDetails(@PathVariable("userId") String userId,@RequestBody List<CartDTO> cartDto)  
	{
		logger.info("in add cart details");
		 return orderService.saveCartDetails(userId,cartDto);
	}
	
	@GetMapping("/{userId}/{cartId}")
	public ResponseEntity<List<CartDetailsDTO>> getCartDetails(@PathVariable("userId") String userId,@PathVariable("cartId") String cartId)  
	{
		logger.info("in add cart details");
		 return orderService.getCartDetails(userId,cartId);
	}
	
	@PostMapping("/")
	public ResponseEntity<String> productOrder(@RequestBody OrderProductDTO orderProDto)  throws InSufficientBalanceException, ResourceNotFoundException
	{
		logger.info("in product Order");
		 return new ResponseEntity<>(orderService.orderProducts(orderProDto), HttpStatus.OK);
	}
	
	@GetMapping("/orderHistory/{userName}/{userId}")
	public ResponseEntity<List<OrderHistoryDTO>> orderHistory(@PathVariable("userName") String userName,@PathVariable("userId") String userId) 
	{
		 return new ResponseEntity<>(orderService.getOrderHistory(userName,userId), HttpStatus.OK);
	}
}
