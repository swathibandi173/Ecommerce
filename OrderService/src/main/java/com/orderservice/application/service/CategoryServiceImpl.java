package com.orderservice.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orderservice.application.model.Category;
import com.orderservice.application.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService
{

	@Autowired
	CategoryRepository catRepository;
	
	@Override
	public Category saveCategory(Category catgry) 
	{
		return catRepository.save(catgry);
		
	}
	
}
