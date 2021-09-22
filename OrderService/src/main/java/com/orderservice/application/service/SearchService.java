package com.orderservice.application.service;

import java.util.List;

import com.orderservice.application.dto.SearchDTO;

public interface SearchService 
{
	List<SearchDTO> findProductDetails(String productName, String catName);
}
