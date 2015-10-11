package com.bankapp.validators;
import java.security.SecureRandom;
import java.util.Random;

public class OTPGenerator {

	public static String generateOTP() {
	        String chars = "abcdefghijklmnopqrstuvwxyz"
	                     + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
	                     + "0123456789";

	        final int pw_len = 7;
	        Random rnd = new SecureRandom();
	        StringBuilder pass = new StringBuilder();
	        for (int i = 0; i < pw_len; i++)
	            pass.append(chars.charAt(rnd.nextInt(chars.length())));
	        return pass.toString();
	    }

	}

	
	

