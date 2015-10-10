package com.bankapp.services;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;

public interface IUserService {
    User registerNewUserAccount(User accountDto) throws EmailExistsException;

    User getUserById(Long id);
    
    User getUserByEmail(String email);

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String VerificationToken);
}
