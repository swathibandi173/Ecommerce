package com.bankservice.application.exception;

public class BeneficiaryAccountNoAlreadyExistsException extends RuntimeException
{
	  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	public BeneficiaryAccountNoAlreadyExistsException()
	  {
		  super();
	  }
	  public BeneficiaryAccountNoAlreadyExistsException(String message)
	  {
		  super(message);
	  }
	  
	  public BeneficiaryAccountNoAlreadyExistsException(String message, Throwable t)
	  {
		  super(message,t);
	  }


}
