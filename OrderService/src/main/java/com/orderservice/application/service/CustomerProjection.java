package com.orderservice.application.service;

import java.util.List;

public interface CustomerProjection
{
	public String getFirstName();
	public String getLastName();
	public List<AccountDetails> getAccountDetails();
	interface AccountDetails
	 {
		  public long getAccountNumber() ;
		  public String getAccountType();
		  public double getAvaialableBalance() ;
		  public String getIfsccode();
	 }
}
