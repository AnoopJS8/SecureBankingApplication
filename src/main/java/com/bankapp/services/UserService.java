
package com.bankapp.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.OTPRepository;
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
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        return user;
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

    // OTP Part
    @Override
    public OneTimePassword generateOTP(Transaction transaction) {
        OneTimePassword otp = new OneTimePassword(transaction);
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

    public boolean verifyOTP(OneTimePassword otp) {
        OneTimePassword otpFromDB = oTPRepository.findOne(otp.getId());
        if (otp.getValue() == otpFromDB.getValue()) {
            return true;
        } else {
            return false;
        }
    }

	@Override
	public void adduser(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPII(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String AddUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update_name(String username, User user) {
		// TODO Auto-generated method stub
		user.setUsername(username);
		
		userRepository.save(user);
		
		
	}
	
	public void update_Address(String useraddress, User user) {
		// TODO Auto-generated method stub
		user.setAddress(useraddress);
		
		userRepository.save(user);
		
		
	}

	

	@Override
	public User getuserbyName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteuser(Long id) {
		// TODO Auto-generated method stub
		User user = userRepository.findById(id);
		userRepository.delete(user);
		
	}

	@Override
	public List<User> getManagers() {
		// TODO Auto-generated method stub
		
		List<User> getmanager = new ArrayList();
		List<User> user1 = (List<User>) userRepository.findAll();
		for (int i = 0; i < user1.size(); i++) {
			if(user1.get(i).getRole().getId()==1)
			{
				getmanager.add(user1.get(i));
			}
		}
		
		return getmanager;
	}
	
	@Override
	public List<User> getEmployees() {
		// TODO Auto-generated method stub
		
		List<User> getmanager = new ArrayList();
		List<User> user1 = (List<User>) userRepository.findAll();
		for (int i = 0; i < user1.size(); i++) {
			if(user1.get(i).getRole().getId()==6)
			{
				getmanager.add(user1.get(i));
			}
		}
		
		return getmanager;
	}

}