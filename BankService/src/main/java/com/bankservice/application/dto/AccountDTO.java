package com.bankservice.application.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO implements Serializable 
{
	private static final long serialVersionUID = -6581673296616581003L;
	
	private String accountType;
	
	private String openingDeposit;

	private String bankName;

	private String branchName;
	
	private String ifsCode;
	
	private String customerId;

}
