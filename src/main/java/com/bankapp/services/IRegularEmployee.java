package com.bankapp.services;

import java.security.Principal;
import java.util.List;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Transaction;

import com.bankapp.models.VerificationToken;

public interface IRegularEmployee {

Double update_Amount(Double Amount, Transaction transaction);
	

}
