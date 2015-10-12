package com.bankapp.services;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;

public interface IUserService {
	User registerNewUserAccount(User accountDto) throws EmailExistsException;

	User getUserById(Long id);

	User getUser(String verificationToken);

	void saveRegisteredUser(User user);

	void createVerificationToken(User user, String token);

	VerificationToken getVerificationToken(String VerificationToken);

	VerificationToken generateNewVerificationToken(String VerificationToken);

	// OTP Section
	OneTimePassword generateOTP(Transaction transaction);

	OneTimePassword generateNewOTP(String value);
	
	void adduser(User user);
    
    String getPII(User user);
    
    String AddUser(User user);
    
    void updateuser(Long id);

	void deleteuser(Long id);

}
