package com.userregistration.application;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test 
{
	public static void main(String args)
	{
		String password=  new BCryptPasswordEncoder().encode("Swathi@1234");
		
		System.out.println("password:::"+password);
	}
}
