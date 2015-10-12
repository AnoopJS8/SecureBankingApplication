package com.bankapp.services;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OTPVerification;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UsedOTPRepository;
import com.bankapp.repositories.UserRepository;
import com.bankapp.repositories.VerificationTokenRepository;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UsedOTPRepository usedOTPRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Transactional
    @Override
    public User registerNewUserAccount(final User user) throws EmailExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email adress: " + user.getEmail());
        }
        final User newUser = new User();

        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());

        newUser.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

        return repository.save(newUser);
    }

    private boolean emailExist(String email) {
        User user = repository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public User getUserById(Long id) {
        User user = repository.findById(id);
        return user;
    }

    @Override
    public User getUser(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        repository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);

        vToken.setToken(UUID.randomUUID().toString());
        vToken = tokenRepository.save(vToken);
        return vToken;}

    
 // OTP Part
        
        @Override
        public OTPVerification getUsedOTP(String usedOTP) {
            return usedOTPRepository.findByUsedOTP(usedOTP);
        }

        @Override
        public void generateOTP(Transaction transaction, String otp) {
            OTPVerification otp1 = new OTPVerification(otp, transaction);
            usedOTPRepository.save(otp1);
        }

        @Override
        public OTPVerification generateNewOTP(final String existingUsedOTP) {
            OTPVerification newOTP = usedOTPRepository.findByUsedOTP(existingUsedOTP);

//
	        String chars = "abcdefghijklmnopqrstuvwxyz"
                    + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789";
       final int pw_len = 7;
       Random rnd = new SecureRandom();
       StringBuilder pass = new StringBuilder();
       for (int i = 0; i < pw_len; i++)
           pass.append(chars.charAt(rnd.nextInt(chars.length())));

       newOTP.setUsedOTP(pass.toString());
       newOTP = usedOTPRepository.save(newOTP);
            return newOTP;    
    
    
    }
}