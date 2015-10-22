package com.bankapp.services;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OneTimePassword;

import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;
import com.bankapp.repositories.OTPRepository;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UserRepository;
import com.bankapp.repositories.VerificationTokenRepository;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPRepository oTPRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private IMailService mailService;

    @Transactional
    @Override
    public User registerNewUserAccount(final User user, String roleName) throws EmailExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email adress: " + user.getEmail());
        }
        final User newUser = new User();

        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setAddress(user.getAddress());
        newUser.setGender(user.getGender());
        newUser.setDateOfBirth(user.getDateOfBirth());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setSecurityQuestion(user.getSecurityQuestion());
        newUser.setSecurityAnswer(user.getSecurityAnswer());
        newUser.setRole(roleRepository.findByName(roleName));

        return userRepository.save(newUser);
    }

    private boolean emailExist(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id);
        return user;
    }

    @Override
    public User getUser(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    @Override
    public User getUserFromSession(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            if (email != null) {
                User user = userRepository.findByEmail(email);
                return user;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
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
        return vToken;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    @Override
    public void generateTemporaryPassword(User user) {
        String temporaryPassword = OneTimePassword.generateOTP();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);

        String userName = user.getUsername();
        String recipientAddress = user.getEmail();
        String subject = "My ASU Bank - Temporary Password";
        String textBody = String
                .format("Dear %s, <br /><br />Here is your temporary password for your account: %s<br />"
                        + "<br />Regards,<br />My ASU Bank", userName, temporaryPassword);
        mailService.sendEmail(recipientAddress, subject, textBody);
    }


    @Override
    public void updateUser(Long existingUserId, User updatedUser) {
        User existingUser = userRepository.findById(existingUserId);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setGender(updatedUser.getGender());
        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<User> getManagers() {
        Role managerRole = roleRepository.findByName("ROLE_MANAGER");
        return userRepository.findByRole(managerRole);
    }

    @Override
    public List<User> getEmployees() {
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE");
        return userRepository.findByRole(employeeRole);
    }

    @Override
    public boolean verifyPassword(User user, String currentPassword) {
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    @Transactional
    @Override
    public boolean changePassword(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getNewpassword()));
            userRepository.save(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // OTP Part
    @Override
    public OneTimePassword generateOTP(Long resourceId, String resourceName) {
        OneTimePassword otp = oTPRepository.findByresourceIdAndResourceName(resourceId, resourceName);

        if (otp != null) {
            String newOtp = OneTimePassword.generateOTP();
            otp.setValue(newOtp);
        } else {
            otp = new OneTimePassword(resourceId, resourceName);
        }
        oTPRepository.save(otp);
        return otp;
    }

    @Override
    public OneTimePassword generateNewOTP(final String existingUsedOTP) {
        OneTimePassword existingOTP = oTPRepository.findByValue(existingUsedOTP);
        String temp = OneTimePassword.generateOTP();
        existingOTP.setValue(temp);
        existingOTP = oTPRepository.save(existingOTP);
        return existingOTP;

    }


    @Override
    public boolean verifyOTP(String otp, Long id, String name) {
        OneTimePassword otpFromDB = oTPRepository.findByresourceIdAndResourceName(id, name);
        if (otp.equals(otpFromDB.getValue())) {
            oTPRepository.delete(otpFromDB.getId());
            return true;
        } else {
            return false;
        }
    }

}