package com.orderservice.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orderservice.application.model.Category;
import com.orderservice.application.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController
{

	@Autowired
	CategoryService catService;
	
	@PostMapping("/save")
	 @ResponseStatus(value=HttpStatus.BAD_GATEWAY)
	public String saveCategory(@RequestBody Category category)
	{
		Category ctgry = catService.saveCategory(category);
		return "Category created successfully with category Id:"+ctgry.getCategoryId();
	}
}
