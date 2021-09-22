package com.orderservice.application.service;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orderservice.application.dto.SearchDTO;
import com.orderservice.application.exception.InavalidArgumentException;
import com.orderservice.application.exception.SearchResultsNotFoundException;
import com.orderservice.application.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService
{
	private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
	@Autowired
	ProductRepository prodRepository;
	@Override
	public List<SearchDTO> findProductDetails(String productName, String catName)
			throws InavalidArgumentException, SearchResultsNotFoundException 
	{
		logger.info("in find product details");
		if ((Strings.isBlank(productName) && Strings.isBlank(catName)))
			throw new InavalidArgumentException("Invalid Input");
		if ((Strings.isNotBlank(productName) && Strings.isNotBlank(catName)))
			return prodRepository.getProductNameAndCategoryName(productName, catName);
		else {
			List<SearchDTO> searchDTo = prodRepository.getProductNameOrCategoryName(productName, catName);
			if (searchDTo.size() == 0) {
				String searchName = Strings.isBlank(productName) ? catName : productName;
				throw new SearchResultsNotFoundException("No Results found with :: " + searchName);
			} else
				return searchDTo;
		}
	}
}
