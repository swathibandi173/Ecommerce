package com.orderservice.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;

import com.orderservice.application.dto.FundTransferDTO;
import com.orderservice.application.dto.OrderProductDTO;
import com.orderservice.application.dto.ProductDTO;
import com.orderservice.application.model.Cart;
import com.orderservice.application.model.OrderDetails;
import com.orderservice.application.model.Product;
import com.orderservice.application.repository.CartDetailsRepository;
import com.orderservice.application.repository.CartRepository;
import com.orderservice.application.repository.OrderRepository;
import com.orderservice.application.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceTest
{
	@InjectMocks
	OrderServiceImpl orderService;
	
	@Mock
	OrderRepository orderRepository;
	
	@Mock
	ProductRepository productRepository;
	
	@Mock
	CartRepository cartRepository;
	
	@Mock
	CartDetailsRepository cartDetailsRepo;
	
	
	@Mock
	CircuitBreakerFactory circuitBreakerFactory;
	
	@Mock
	org.springframework.cloud.client.circuitbreaker.CircuitBreaker circuitBreaker;
	
	static Order order;
	static Optional<Product> product;
	static OrderDetails orderDetails;
	static OrderProductDTO orderProductDTO;
	static ProductDTO productDTO;
	static FundTransferDTO fundTransferDTO ;
	static Cart cart;
	
	@BeforeAll
	public static void setUp()
	{
		productDTO = new ProductDTO();
		productDTO.setProductId("1");
		productDTO.setQuantity("10");
		
		
		orderProductDTO = new OrderProductDTO();
		orderProductDTO.setAccountNo("12346789123");
		orderProductDTO.setUserId("1");
		orderProductDTO.setUserName("swathi");
		orderProductDTO.setCartId("1");
		//orderProductDTO.setProductDto(listProdDTO);
		
		fundTransferDTO = new FundTransferDTO();
		fundTransferDTO.setFromAccountNo("1234");
		fundTransferDTO.setToAccountNo("67777");
		fundTransferDTO.setTransferAmount("100");
		fundTransferDTO.setRemarks("DEBIT");
		
		cart = new Cart();
		cart.setUserId("1");

	}
	

	@Test
	@DisplayName("Order placed :: Postive Scenario")
	@Order(1)
	public void testOrderCreationPostive() 
	{
		
		Mockito.when(productRepository.findById(1l)).thenReturn(product);
		
		Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenAnswer(i -> {
			Cart cart = i.getArgument(0);
			cart.setCartId(1l);
			return cart;
		});
		
		Mockito.when(cartRepository.findByUserIdAndCartId("1",1l)).thenReturn(cart);
		
		org.springframework.cloud.client.circuitbreaker.CircuitBreaker cruitBreaker= orderService.getCircuitBreaker();
		
		
		assertEquals(HttpStatus.OK,orderService.orderProducts(orderProductDTO));
	}
	

}	
	