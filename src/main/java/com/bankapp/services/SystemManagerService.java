package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bankapp.models.Account;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;
import com.bankapp.repositories.OTPRepository;
import com.bankapp.repositories.TransactionRepository;
import com.bankapp.repositories.UserRepository;

@Service
public class SystemManagerService implements ISystemManagerService {

    @Autowired
    private UserRepository UserRepo;

    @Autowired
    private TransactionRepository TransRepo;

    @Autowired
    private AccountRepository AccountRepo;
    


    @Override
    public List<Transaction> getTransactionsByStatus(String status) {

        List<Transaction> list = TransRepo.findByStatus(status);
        System.out.println(list);
        return list;
    }

    public User viewUserById(Long id) {
        User user = UserRepo.findById(id);
        return user;
    }

    public User viewUserByEmail(String email) {
        User user = UserRepo.findByEmail(email);
        return user;
    }

    public User addUser(User user) {
        UserRepo.save(user);
        return user;
    }

    public String approveTransaction(Transaction transaction) {

        String result = "";
        transaction.setStatus("Approved");

        try {
            TransRepo.save(transaction);
            result = "Successull";
            System.out.println("Done approve");
        } catch (Exception e) {
            result = "unsuccessull";
        }

        return result;
    }

    @Override
    public Transaction getTransactionbyid(Long id) {
        Transaction transaction = TransRepo.findOne(id);
        return transaction;
    }

    public String reflectChangesToSender(Account account, Double balance, Double amount) {
        try {
            account.setBalance(balance - amount);
            AccountRepo.save(account);

        } catch (Exception e) {
            return e.getMessage();
        }

        return "Success";
    }

    public String reflectChangesToReceiver(Account account, Double balance, Double amount) {
        try {
            account.setBalance(balance + amount);
            AccountRepo.save(account);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "Success";
    }
    
    
   

	
}