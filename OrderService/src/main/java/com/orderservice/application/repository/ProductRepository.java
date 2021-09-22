package com.orderservice.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.orderservice.application.dto.SearchDTO;
import com.orderservice.application.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	@Query("select new com.orderservice.application.dto.SearchDTO(pro.category.categoryName,pro.category.categoryDescrption, "
			+ "pro.productId,pro.productName,pro.productPrice,pro.productQuantity) "
			+ " from Product pro"
			+ " where (pro.productName=:productName or pro.category.categoryName=:catName) ")
	List<SearchDTO> getProductNameOrCategoryName(String productName,String catName);
	
	
	@Query("select new com.orderservice.application.dto.SearchDTO(pro.category.categoryName,pro.category.categoryDescrption,pro.productId,pro.productName,pro.productPrice,pro.productQuantity) "
			+ " from Product pro"
			+ " where (pro.productName=:productName and pro.category.categoryName=:catName)")
	List<SearchDTO> getProductNameAndCategoryName(String productName,String catName);
	
}
