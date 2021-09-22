package com.orderservice.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundTransferDTO 
{
	 private String fromAccountNo;
	 private String toAccountNo;
     private String transferAmount;
	 private String remarks;
}
