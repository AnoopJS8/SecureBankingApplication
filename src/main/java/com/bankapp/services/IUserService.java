
package com.bankapp.services;

import java.security.Principal;
import java.util.List;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;

public interface IUserService {
    User registerNewUserAccount(User accountDto, String roleName) throws EmailExistsException;

    User getUserById(Long id);

    User getUserByEmail(String email);

    User getUser(String verificationToken);

    User getUserFromSession(Principal principal);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String VerificationToken);

    OneTimePassword generateOTP(Long resourceId, String resourceName);

    void adduser(User user);

    String getPII(User user);

    String addUser(User user);

    void updateUser(Long existingUserId, User newUser);

    void deleteUser(User user);

    List<User> getManagers();

    List<User> getEmployees();

    // OTP Section
    OneTimePassword generateOTP(Transaction transaction);

    OneTimePassword generateNewOTP(String value);

    void generateTemporaryPassword(User user);

    boolean verifyOTP(OneTimePassword otp);

}
