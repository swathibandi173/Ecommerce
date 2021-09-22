package com.bankservice.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankservice.application.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{

}
