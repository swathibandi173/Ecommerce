package com.orderservice.application.feignclientservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.orderservice.application.dto.FundTransferDTO;
import com.orderservice.application.response.AccountResponse;

@FeignClient(name = "http://BANK-SERVICE:8003/bankservice")
public interface BankServiceOperations
{
	@GetMapping("/{accountNo}")
	public ResponseEntity<AccountResponse> getAccountAvlBal(@PathVariable("accountNo") String accountNo);

	@PostMapping("/fundTransfer")
	public ResponseEntity<HttpStatus> fundTransfer(@RequestBody FundTransferDTO fundTransferDto);

}
