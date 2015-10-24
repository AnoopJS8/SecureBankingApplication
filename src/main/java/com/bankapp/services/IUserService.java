package com.bankapp.services;

import java.security.Principal;
import java.util.List;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;

public interface IUserService {
    
    public User addEmployee(User user, String roleName) throws EmailExistsException;
        
    User registerNewUserAccount(User accountDto, String roleName) throws EmailExistsException;

    User getUserById(Long id);

    User getUserByEmail(String email);

    User getUser(String verificationToken);

    User getUserFromSession(Principal principal);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String VerificationToken);

    void updateUser(Long existingUserId, User newUser);

    void deleteUser(User user);

    List<User> getManagers();

    List<User> getEmployees();

    void generateTemporaryPassword(User user);

    boolean changePassword(User user);
    
    boolean emailExist(String email);
    
    boolean idExist(Long id);

    public boolean verifyPassword(User user, String currentPassword);

    // OTP Section
    OneTimePassword generateOTP(Long resourceId, String resourceName);

    OneTimePassword generateNewOTP(String value);

    boolean verifyOTP(String otp, Long id, String name);
    
    
}