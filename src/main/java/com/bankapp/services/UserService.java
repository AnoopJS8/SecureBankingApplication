package com.bankapp.services;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.encryption.RSAKeyPair;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.Account;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Role;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;
import com.bankapp.repositories.AccountRepository;
import com.bankapp.repositories.OTPRepository;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.TransactionRepository;
import com.bankapp.repositories.UserRepository;
import com.bankapp.repositories.VerificationTokenRepository;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transRepository;

    @Autowired
    private OTPRepository oTPRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

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

    public boolean emailExist(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    public boolean idExist(String id) {
        User user = userRepository.findById(id);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public User getUserById(String id) {
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
    public boolean hasMissingFields(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            if (email != null) {
                User user = userRepository.findByEmail(email);
                if (user.getSecurityAnswer() == null || user.getSecurityQuestion() == null) {
                    return true;
                }
                return false;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        try {
            RSAKeyPair keyPair = new RSAKeyPair(2048);
            user.setPublicKey(keyPair.getPrivateKey());
            String publicKey = new String(Base64.encodeBase64(keyPair.getPublicKey()));
            String userName = user.getUsername();
            String recipientAddress = user.getEmail();
            String subject = "My ASU Bank - Security Feature";
            String textBody = String.format(
                    "Dear %s, <br /><br />As a valued customer, we respect your privacy "
                    + "and ensure that your account is alwasy secured. <br /><br />"
                    + "Please download our transaction verifier, and use the below provided "
                    + "PIN to encrypt your transactions.<br /><br />%s<br /><br />"
                    + "Download the transaction verifier from: <a href='%s'>HERE</a>"
                    + "<br />Regards,<br />My ASU Bank", userName, publicKey, "DUMMY");

            mailService.sendEmail(recipientAddress, subject, textBody);
            userRepository.save(user);
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
    public void updateUser(String existingUserId, User updatedUser) {
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
    public List<User> getCustomers() {
        Role role = roleRepository.findByName("ROLE_CUSTOMER");
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getMerchants() {
        Role role = roleRepository.findByName("ROLE_MERCHANT");
        return userRepository.findByRole(role);
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
            user.setNewpassword(null);
            userRepository.save(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // OTP Part
    @Override
    public OneTimePassword generateOTP(String resourceId, String resourceName) {
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
    public boolean verifyOTP(String otp, String id, String name) {
        OneTimePassword otpFromDB = oTPRepository.findByresourceIdAndResourceName(id, name);
        if (otp.equals(otpFromDB.getValue())) {
            oTPRepository.delete(otpFromDB.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User addEmployee(User user, String roleName) throws EmailExistsException {

        String temporaryPassword = OneTimePassword.generateOTP();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setRole(roleRepository.findByName(roleName));
        user.setEnabled(true);
        userRepository.save(user);

        String userName = user.getUsername();
        String recipientAddress = user.getEmail();
        String subject = "My ASU Bank - New Account Creation";
        String role = user.getRole().getName();

        if (role.equalsIgnoreCase("ROLE_MANAGER"))
            role = "MANAGER";
        else if (role.equalsIgnoreCase("ROLE_ADMIN"))
            role = "ADMIN";
        else
            role = "EMPLOYEE";

        String textBody = String
                .format("Dear %s, <br /><br />You are now registered as %s. Here is your temporary password : %s<br />"
                        + "<br />Regards,<br />My ASU Bank", userName, role, temporaryPassword);
        mailService.sendEmail(recipientAddress, subject, textBody);

        return user;
    }

    @Override
    public List<User> displayDeleteUsers() {
        List<User> users = userRepository.findByIsDeleted(true);

        return users;
    }

    @Override
    public void deleteExternalUser(User user) {
        Account account = accountRepository.findByUser(user);

        VerificationToken verifyuser = tokenRepository.findByUser(user);
        if (verifyuser != null) {
            tokenRepository.delete(verifyuser);
        }

        if (account != null) {
            List<Transaction> transactionfromAccount = transRepository.findByFromAccount(account);
            List<Transaction> transactiontoAccount = transRepository.findByToAccount(account);

            for (int i = 0; i < transactionfromAccount.size(); i++) {
                transRepository.delete(transactionfromAccount.get(i));
            }

            for (int i = 0; i < transactiontoAccount.size(); i++) {
                transRepository.delete(transactiontoAccount.get(i));
            }

            accountRepository.delete(account);
        }

        userRepository.delete(user);

    }

}