package com.bankapp.services;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Role;
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
	    private UserRepository repository;

	    @Autowired
	    private OTPRepository oTPRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Autowired
	    private RoleRepository roleRepository;

	    @Autowired
	    private VerificationTokenRepository tokenRepository;

	@Transactional
	@Override
	public User registerNewUserAccount(final User user)
			throws EmailExistsException {
		if (emailExist(user.getEmail())) {
			throw new EmailExistsException(
					"There is an account with that email adress: "
							+ user.getEmail());
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

	

	// OTP Part

	
	@Override
	public OneTimePassword generateNewOTP(final String existingUsedOTP) {
		OneTimePassword existingOTP = oTPRepository
				.findByValue(existingUsedOTP);

		String temp = OneTimePassword.generateOTP();

		existingOTP.setValue(temp);
		existingOTP = oTPRepository.save(existingOTP);
		return existingOTP;

	}
	
	public void adduser(User user) {
		// TODO Auto-generated method stub
		repository.save(user);
	
	}

	@Override
	public void deleteuser(Long l) {
		// TODO Auto-generated method stub
		
		User user1 = getUserById((long)l);
		repository.delete(user1);
		
	}

	@Override
	public void updateuser(Long l) {
		// TODO Auto-generated method stub
		
		
		User user2 = getUserById(l);
		user2.setEmail("change@gmail.com");
			
				
		
	}

	@Override
	public String getPII(User user) {
		// TODO Auto-generated method stub
		if(repository.findById(user.getId())!=null)
		{
			return "authorized";
		}
		else
		{
			return "No authorized";
		}
		
	}

	@Override
	public String AddUser(User user) {
		repository.save(user);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUser(String verificationToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VerificationToken generateNewVerificationToken(String VerificationToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OneTimePassword generateOTP(Transaction transaction) {
		// TODO Auto-generated method stub
		return null;
	}
}