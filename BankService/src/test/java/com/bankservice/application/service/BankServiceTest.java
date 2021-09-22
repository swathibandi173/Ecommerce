package com.bankservice.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bankservice.application.dto.AccountResponse;
import com.bankservice.application.dto.FundTransferDto;
import com.bankservice.application.exception.InvalidAccountNoException;
import com.bankservice.application.mapper.FundTransferMapper;
import com.bankservice.application.model.Account;
import com.bankservice.application.model.Transaction;
import com.bankservice.application.repository.AccountRepository;
import com.bankservice.application.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceTest {

	@Mock
	AccountRepository accountRepository;

	@Mock
	TransactionRepository transactionRepository;

	@Mock
	FundTransferMapper fundTransferMapper;

	static Account account;

	static Transaction transaction;

	static AccountResponse response;

	static FundTransferDto fundTransferDto = new FundTransferDto();

	@InjectMocks
	BankServiceImpl bankServiceImpl;

	@BeforeAll
	public static void setUp() {

		response = new AccountResponse();
		response.setAccountNo(1l);
		response.setAvailableBalance(1000);

		account = new Account();
		account.setAccountId(1l);
		account.setAccountNo(1l);
		account.setAccountType("cecking");
		account.setAvailableBalance(10000);

		transaction = new Transaction();

		transaction.setAccount(account);
		transaction.setAmount(1000);
		transaction.setFromAccount(1l);

		fundTransferDto.setFromAccountNo("1234");
		fundTransferDto.setToAccountNo("67777");
		fundTransferDto.setTransferAmount("100");
		fundTransferDto.setRemarks("DEBIT");

	}

	@Test
	@DisplayName("Avaiable Balance :: Postive Scenario")
	@Order(1)
	public void testAvailableBalancePostive() {
		Mockito.when(accountRepository.findByAccountNo(Long.valueOf("1234"))).thenReturn(Optional.of(account));

		ResponseEntity<AccountResponse> result = bankServiceImpl.getAccountAvlBal("1234");

		assertEquals(HttpStatus.OK, result.getStatusCode());

	}

	@Test
	@DisplayName("Avaiable Balance :: Negative Scenario")
	@Order(2)
	public void testAvailableBalanceNegative() {
		Mockito.when(accountRepository.findByAccountNo(Long.valueOf("1234")))
				.thenThrow(InvalidAccountNoException.class);

		assertThrows(InvalidAccountNoException.class, () -> bankServiceImpl.getAccountAvlBal("1234"));

	}

	@Test
	@DisplayName("FundTransfer :: Postive Scenario")
	@Order(3)
	public void testFundTransferPostive() {
		Mockito.when(accountRepository.findByAccountNo(Long.valueOf("1234"))).thenReturn(Optional.of(account));

		Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(i -> {
			Transaction transaction = i.getArgument(0);
			transaction.setTransactionId(1l);
			return transaction;
		});

		Mockito.when(fundTransferMapper.mapToTransaction(fundTransferDto, Optional.of(account).get()))
				.thenReturn(transaction);

		Mockito.when(fundTransferMapper.mapToAccount(fundTransferDto, Optional.of(account).get())).thenReturn(account);

		ResponseEntity<HttpStatus> result = bankServiceImpl.fundTransfer(fundTransferDto);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	@DisplayName("FundTransfer :: Negative Scenario")
	@Order(4)
	public void testFundTransferNegative() {
		Mockito.when(accountRepository.findByAccountNo(Long.valueOf("1234")))
				.thenThrow(InvalidAccountNoException.class);

		assertThrows(InvalidAccountNoException.class, () -> bankServiceImpl.fundTransfer(fundTransferDto));

	}

}
