package com.bankservice.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bankservice.application.dto.FundTransferDto;
import com.bankservice.application.model.Account;
import com.bankservice.application.model.Transaction;

@Mapper(componentModel="spring")
public interface FundTransferMapper
{
	public static final String DEBIT = "DEBIT";
	
	@Mapping(target = "transactionTime", expression = "java( java.time.LocalDateTime.now())")
	@Mapping(target = "transactionType", expression = "java(DEBIT)")
	@Mapping(target = "amount", expression = "java(Double.valueOf(fundTransDto.getTransferAmount()))")
	@Mapping(target = "fromAccount", expression = "java(Long.valueOf(fundTransDto.getFromAccountNo()))")
	@Mapping(target = "toAccount", expression = "java(Long.valueOf(fundTransDto.getToAccountNo()))")
	@Mapping(target = "account", source = "account")

	Transaction mapToTransaction(FundTransferDto fundTransDto, Account account);
	@Mapping(target = "transactionDetails", ignore = true)
	@Mapping(target = "availableBalance", expression = "java(account.getAvailableBalance()-Double.valueOf(fundTransDto.getTransferAmount()))")
	Account mapToAccount(FundTransferDto fundTransDto,Account account);
}
