package com.orderservice.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orderservice.application.model.OrderDetails;

@Repository
public interface OrderHistory extends JpaRepository<OrderDetails, Long>{

}
