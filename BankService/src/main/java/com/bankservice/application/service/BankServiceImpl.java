package com.bankservice.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bankservice.application.dto.AccountResponse;
import com.bankservice.application.dto.FundTransferDto;
import com.bankservice.application.exception.InvalidAccountNoException;
import com.bankservice.application.exception.TransactionFailedException;
import com.bankservice.application.mapper.FundTransferMapper;
import com.bankservice.application.model.Account;
import com.bankservice.application.repository.AccountRepository;
import com.bankservice.application.repository.TransactionRepository;

@Service
public class BankServiceImpl 
{

	@Autowired
	AccountRepository accRepo;
	
	@Autowired
	TransactionRepository transRepo;
	
	
	@Autowired
	FundTransferMapper fundTransferMapper;
	
	
	
	public ResponseEntity<AccountResponse>  getAccountAvlBal(String accountNo)
	{
		 Account acct = getAccountDetails(accountNo).orElseThrow(()->new InvalidAccountNoException("Account no does not exits"));
		 AccountResponse response = new AccountResponse();
		 response.setAccountNo(acct.getAccountNo());
		 response.setAvailableBalance(acct.getAvailableBalance());
		 
		 return new ResponseEntity<>(response,HttpStatus.OK);
		
	}
	
	@Transactional(rollbackFor = TransactionFailedException.class, isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRES_NEW)
	public ResponseEntity<HttpStatus> fundTransfer(FundTransferDto fundTransDto)
	{
		 Account fromAccDetails = getAccountDetails(fundTransDto.getFromAccountNo()).orElseThrow(()->new InvalidAccountNoException("Account no does not exits"));
		
			transRepo.save(fundTransferMapper.mapToTransaction(fundTransDto, fromAccDetails));
			accRepo.save(fundTransferMapper.mapToAccount(fundTransDto,fromAccDetails));		
			return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	private Optional<Account> getAccountDetails(String accountNo)
	{
		return accRepo.findByAccountNo(Long.valueOf(accountNo));
				
	}
	
	public ResponseEntity<List<Account>> getAllAccounts()
	{
		return new ResponseEntity<>(accRepo.findAll(),HttpStatus.OK);
	}
	
	
	
}
