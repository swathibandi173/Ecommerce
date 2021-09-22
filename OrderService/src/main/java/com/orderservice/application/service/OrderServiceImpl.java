package com.orderservice.application.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.AtomicDouble;
import com.orderservice.application.dto.CartDTO;
import com.orderservice.application.dto.CartDetailsDTO;
import com.orderservice.application.dto.FundTransferDTO;
import com.orderservice.application.dto.OrderHistoryDTO;
import com.orderservice.application.dto.OrderHistoryDetailsDTO;
import com.orderservice.application.dto.OrderProductDTO;
import com.orderservice.application.dto.ProductDTO;
import com.orderservice.application.exception.CartDetailsNotFoundException;
import com.orderservice.application.exception.InSufficientBalanceException;
import com.orderservice.application.exception.OrderNotFoundException;
import com.orderservice.application.exception.OutOfStockException;
import com.orderservice.application.exception.ResourceNotFoundException;
import com.orderservice.application.feignclientservice.BankServiceOperations;
import com.orderservice.application.feignclientservice.UserServiceOperations;
import com.orderservice.application.model.Cart;
import com.orderservice.application.model.CartDetails;
import com.orderservice.application.model.Order;
import com.orderservice.application.model.OrderDetails;
import com.orderservice.application.model.Product;
import com.orderservice.application.repository.CartDetailsRepository;
import com.orderservice.application.repository.CartRepository;
import com.orderservice.application.repository.OrderHistory;
import com.orderservice.application.repository.OrderRepository;
import com.orderservice.application.repository.ProductRepository;
import com.orderservice.application.response.AccountResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	OrderRepository orderRepo;

	@Autowired
	CartRepository cartRepo;

	@Autowired
	CartDetailsRepository cartDetailsRepo;

	@Autowired
	ProductRepository prodRepo;

	@Autowired
	OrderHistory orderHistoryRepo;

	@Autowired
	CircuitBreakerFactory circuitBreakerFactory;

	@Autowired
	BankServiceOperations bankServiceOperations;

	@Autowired
	UserServiceOperations userServiceOperations;

	@Transactional
	public String orderProducts(OrderProductDTO orderProdDto) {

		String s1 = String.format("Order Product Method :: %s", orderProdDto.getUserId());
		logger.info(s1);

		String userId = orderProdDto.getUserId();

		Cart cart = Optional
				.ofNullable(cartRepo.findByUserIdAndCartId(userId, Long.valueOf(orderProdDto.getCartId()).longValue()))
				.orElseThrow(() -> new CartDetailsNotFoundException("Cart id " + orderProdDto.getCartId()
						+ "  not assocated with the user.please check.... or Cart details are not found  "));

		List<ProductDTO> productDtoList = new ArrayList<>();
		// Check is the account number exists, if does not exists return error message
		// else continue .

		Object responseAccObject = getAvaialbleBalance(orderProdDto.getAccountNo()).getBody();

		if (responseAccObject instanceof String)
			return responseAccObject.toString();

		long toAccDetails = 31787544391l;// Hardcoded Ecommerce Account No.....

		AccountResponse accResponse = (AccountResponse) responseAccObject;

		double availableBal = 0.00d;
		String accountNo = "";

		if (Optional.ofNullable(accResponse).isPresent()) {
			availableBal = accResponse.getAvailableBalance();
			accountNo = "" + accResponse.getAccountNo();
		}

		cart.getCartList().stream().map(prod -> {
			ProductDTO dto = new ProductDTO();
			// Check is the product with the product id exists, if does not exists return
			// error message else continue .
			Product product = prodRepo.findById(Long.valueOf(prod.getProductId()))
					.orElseThrow(() -> new ResourceNotFoundException(
							"Product with Product Id " + prod.getProductId() + " not found "));

			if (prod.getQuantity() > product.getProductQuantity()) {
				throw new OutOfStockException("requested quantity for the product :: " + product.getProductName() + "("
						+ product.getProductId() + ") " + " not available in the stock, available stock is:: "
						+ product.getProductQuantity());
			}
			dto.setProductId(String.valueOf(product.getProductId()));
			dto.setQuantity("" + prod.getQuantity());
			dto.setTotalPrice(product.getProductPrice() * prod.getQuantity());
			productDtoList.add(dto);
			return productDtoList;
		}).collect(Collectors.toList());

		Double totalAmount = productDtoList.stream().map(totPrice -> totPrice.getTotalPrice())
				.collect(Collectors.summingDouble(Double::doubleValue));

		// Checks if the total purchase amount is greater that available balance
		if (totalAmount > availableBal) {
			throw new InSufficientBalanceException("InSufficent balance for the account number::" + accountNo);
		}
		// if all the validations are success then only proceed with placing the order
		Order order = new Order();
		try {
			List<OrderDetails> orderDetailsList = new ArrayList<>();
			order.setDatetime(LocalDateTime.now());
			order.setUserId(userId);
			order.setTotalPrice(totalAmount);
			for (ProductDTO dto : productDtoList) {
				Product product = prodRepo.findById(Long.parseLong(dto.getProductId())).get();
				product.setProductQuantity(product.getProductQuantity() - Integer.parseInt(dto.getQuantity()));
				OrderDetails orderDetails = new OrderDetails();
				orderDetails.setOrders(order);
				orderDetails.setPrice(product.getProductPrice());
				orderDetails.setQuantity(Integer.parseInt(dto.getQuantity()));
				orderDetails.setProduct(product);
				orderDetailsList.add(orderDetails);
			}
			order.setOrderList(orderDetailsList);
			orderRepo.save(order);

			FundTransferDTO fundTransferDTO = new FundTransferDTO();
			fundTransferDTO.setFromAccountNo(String.valueOf(orderProdDto.getAccountNo()));
			fundTransferDTO.setToAccountNo(String.valueOf(toAccDetails));
			fundTransferDTO.setRemarks("DEBIT");
			fundTransferDTO.setTransferAmount(String.valueOf(totalAmount));

			saveTransactionInfo(fundTransferDTO).getBody();

			cartRepo.delete(cart);

		} catch (Exception exception) {
			logger.error("Exception occurred while saving the order", exception);
			return exception.getMessage();
		}

		return "Order placed successfully with order id:: " + order.getOrderId();
	}

	public List<OrderHistoryDTO> getOrderHistory(String userName, String userId) throws OrderNotFoundException {
		List<Order> orders = orderRepo.findByUserId(userId);
		List<OrderHistoryDTO> orderDetails = new ArrayList<>();
		if (orders.size() > 0) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
			OrderHistoryDTO orderHistory = new OrderHistoryDTO();
			List<OrderHistoryDetailsDTO> prodList = new ArrayList<>();
			orderHistory.setUserName(userName);

			AtomicDouble totalAmt = new AtomicDouble(0);
			orders.stream().forEach(order -> {
				totalAmt.getAndAdd(order.getTotalPrice());
				order.getOrderList().stream().map(ordr -> {
					OrderHistoryDetailsDTO dto = new OrderHistoryDetailsDTO();
					dto.setProductName(ordr.getProduct().getProductName());
					dto.setPrice(ordr.getProduct().getProductPrice());
					dto.setQuantity(ordr.getQuantity());
					dto.setTotalPrice(ordr.getPrice() * ordr.getQuantity());
					dto.setOrderDate(formatter.format(ordr.getOrders().getDatetime()));
					prodList.add(dto);
					return prodList;
				}).collect(Collectors.toList());
			});
			orderHistory.setTotalAmount(totalAmt.get());
			orderHistory.setOrderDetails(prodList);
			orderDetails.add(orderHistory);
		} else {
			throw new OrderNotFoundException("Order", "User Name", userName);
		}
		return orderDetails;
	}

	public ResponseEntity<String> saveCartDetails(String userId, List<CartDTO> cartDto) {
		Cart cart = new Cart();
		cart.setUserId(userId);
		cartDto.stream().forEach(e -> {
			Product product = prodRepo.findById(Long.parseLong(e.getProductId())).get();
			cart.setDatetime(LocalDateTime.now());
			CartDetails cartDetails = new CartDetails();
			cartDetails.setProductId(e.getProductId());
			cartDetails.setQuantity(Integer.valueOf(e.getQuantity()));
			cartDetails.setPrice(Integer.valueOf(e.getQuantity()) * product.getProductPrice());
			cartDetails.setCart(cart);
			cartRepo.save(cart);
			cartDetailsRepo.save(cartDetails);

		});
		return new ResponseEntity<>("Cart  placed successfully with cart id::" + cart.getCartId(), HttpStatus.OK);
	}

	public ResponseEntity<List<CartDetailsDTO>> getCartDetails(String userId, String cartId) {
		Cart cart = Optional.ofNullable(cartRepo.findByUserIdAndCartId(userId, Long.valueOf(cartId).longValue()))
				.orElseThrow(() -> new CartDetailsNotFoundException("Cart Details not found for User"));
		List<CartDetailsDTO> listDTO = new ArrayList<>();

		cart.getCartList().stream().forEach(e -> {
			Product product = prodRepo.findById(Long.parseLong(e.getProductId())).get();
			CartDetailsDTO dto = new CartDetailsDTO();
			dto.setProductName(product.getProductName());
			dto.setPrice("" + e.getPrice());
			dto.setQuantity("" + e.getQuantity());
			listDTO.add(dto);
		});
		;
		return new ResponseEntity(listDTO, HttpStatus.OK);
	}

	public org.springframework.cloud.client.circuitbreaker.CircuitBreaker getCircuitBreaker()
	{
		org.springframework.cloud.client.circuitbreaker.CircuitBreaker circuitbreaker = circuitBreakerFactory
				.create("circuitbreaker");
		return circuitbreaker;
	}
	public ResponseEntity<?> getUserDetails(String userName) 
	{
		return getCircuitBreaker().run(() -> userServiceOperations.getUserDetails(userName),
				throwable -> getUserServiceInfo());
	}

	public ResponseEntity<?> getAvaialbleBalance(String accountNo) 
	{
		return getCircuitBreaker().run(() -> bankServiceOperations.getAccountAvlBal(accountNo),
				throwable -> getBankServiceInfo());
	}

	public ResponseEntity<?> saveTransactionInfo(FundTransferDTO fundTransDto) 
	{
		return getCircuitBreaker().run(() -> bankServiceOperations.fundTransfer(fundTransDto),
				throwable -> getBankServiceInfo());
	}

	private ResponseEntity<String> getUserServiceInfo() {
		return new ResponseEntity<>("User Service is dowm.Please try after some time!....",
				HttpStatus.SERVICE_UNAVAILABLE);
	}

	private ResponseEntity<HttpStatus> getBankServiceInfo() {
		return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
	}
}
