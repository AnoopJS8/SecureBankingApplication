package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.TransactionRepository;

@Service
public class TransactionService implements ITransactionService, Constants {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IAccountService accountService;

    @Transactional
    @Override
    public List<Transaction> getTransactionsByAccount(Account fromAccount, Account toAccount) {
        List<Transaction> list = transactionRepository.findByFromAccountOrToAccountOrderByCreatedAsc(fromAccount,
                toAccount);
        return list;
    }

    @Transactional
    @Override
    public String saveTransaction(Transaction transaction, User user) {
        try {
            Long accId = transaction.getToAccount().getAccId();
            transaction.setToAccount(accountService.getAccountByAccountId(accId));
            transaction.setFromAccount(accountService.getAccountsByUser(user));
            transaction.setUser(user);
            String message = accountService.updateBalance(transaction);
            if (message != null) {
                if (message.equalsIgnoreCase(LESS_BALANCE)) {
                    return LESS_BALANCE;
                } else {
                	transaction.setStatus(S_VERIFIED);
                	transactionRepository.save(transaction);
                    return message;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }

        return null;
    }

    @Transactional
	@Override
	public String askCustomerPayment(Transaction transaction, User user) {
		Long accId = transaction.getToAccount().getAccId();
		try{
			transaction.setToAccount(accountService.getAccountsByUser(user));
	        transaction.setFromAccount(accountService.getAccountByAccountId(accId));
	        transaction.setUser(user);
	        transaction.setStatus(S_PENDING_CUSTOMER_VERIFICATION);
        	transactionRepository.save(transaction);
        	return SUCCESS;
        }catch(Exception e){
        	e.printStackTrace();
        	return ERROR;
        }
	}

    @Transactional
	@Override
	public String initiateTransaction(Transaction transaction, User user) {
		Long accId = transaction.getToAccount().getAccId();
		try{
			transaction.setFromAccount(accountService.getAccountsByUser(user));
	        transaction.setToAccount(accountService.getAccountByAccountId(accId));
	        transaction.setUser(user);
	        transaction.setStatus(S_PENDING);
        	transactionRepository.save(transaction);
        	return SUCCESS;
        }catch(Exception e){
        	e.printStackTrace();
        	return ERROR;
        }
	}

}
