package com.orderservice.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="product")
public class Product 
{
	@Id
	@GeneratedValue(generator="product_id",strategy = GenerationType.AUTO )
	@Column(name="product_id")
	private Long productId;
	@Column(name="product_name")
	private String productName;
	@Column(name="product_desc")
	private String productDescription;
	@Column(name="product_price")
	private double productPrice;
	@Column(name="product_quantity")
	private int productQuantity;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category;
	
}
