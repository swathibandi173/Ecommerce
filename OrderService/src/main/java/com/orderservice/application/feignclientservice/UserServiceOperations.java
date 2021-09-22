package com.orderservice.application.feignclientservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.orderservice.application.dto.UserDTO;
import com.orderservice.application.exception.ResourceNotFoundException;

@FeignClient(name="http://USER-REGISTRATION:8001/users")
public interface UserServiceOperations 
{
	@GetMapping("/{userName}")
	public ResponseEntity<UserDTO> getUserDetails(@PathVariable("userName") String userName) throws ResourceNotFoundException; 
}
