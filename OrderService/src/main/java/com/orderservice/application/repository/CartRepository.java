package com.orderservice.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderservice.application.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>
{
	Cart findByUserIdAndCartId(String userId,long cartId);
}
