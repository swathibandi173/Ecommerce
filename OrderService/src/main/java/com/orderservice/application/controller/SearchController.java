package com.orderservice.application.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderservice.application.dto.SearchDTO;
import com.orderservice.application.request.SearchRequest;
import com.orderservice.application.service.SearchService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orderservice")
@Slf4j
public class SearchController 
{
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	@Autowired
	SearchService searchService;

	@PostMapping("/searchproducts")
	public ResponseEntity<List<SearchDTO>> getCategoryProductDetails(@RequestBody SearchRequest request) 
	{
		logger.info("inside Search Controller");
		
		return new ResponseEntity<>(
				searchService.findProductDetails(request.getProductName(), request.getCategoryName()), HttpStatus.OK);
	}


}
