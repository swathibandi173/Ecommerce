package com.bankservice.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankservice.application.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long>
{
	Optional<Account> findByAccountNo(long accountNo);
}
