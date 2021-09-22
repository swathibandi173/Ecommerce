package com.orderservice.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO 
{
	private String accountType;

	private String openingDeposit;

	private String bankName;

	private String branchName;
	
	 private String ifsCode;
	 
	 private String customerId;

}
