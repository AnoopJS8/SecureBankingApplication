
package com.bankapp.services;

import java.util.Date;
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

        String accId = transaction.getToAccount().getAccId();
        Account toAccount = accountService.getAccountByAccountId(accId);
        if (toAccount == null) {
            return ERR_ACCOUNT_NOT_EXISTS;
        }
        Account fromAccount = accountService.getAccountByUser(user);
        transaction.setToAccount(toAccount);
        transaction.setFromAccount(fromAccount);
        Date date = new Date();
        transaction.setTransferDate(date);

        if (fromAccount.getBalance() < transaction.getAmount()) {
            return LESS_BALANCE;
        } else {
            boolean ifCritical = isBelowCriticalLimit(fromAccount, transaction);
            if (ifCritical) {
                transaction.setStatus(S_OTP_PENDING);
                transactionRepository.save(transaction);
                return CRITICAL;
            }
            String message = accountService.updateBalance(transaction);
            transaction.setStatus(S_VERIFIED);
            transactionRepository.save(transaction);
            return message;
        }

    }

    private boolean isBelowCriticalLimit(Account fromAccount, Transaction transaction) {
        double criticalLimit = fromAccount.getCriticalLimit();
        if (transaction.getAmount() >= criticalLimit) {
            return true;
        }
        return false;
    }

    @Transactional
	@Override
	public String askCustomerPayment(Transaction transaction, User user) {
		try {
	        if(transaction.getFromAccount() == null){
	            return ERR_ACCOUNT_NOT_EXISTS;
	        }
			transaction.setToAccount(accountService.getAccountByUser(user));
			transaction.setStatus(S_PENDING_CUSTOMER_VERIFICATION);
			Date date = new Date();
			transaction.setTransferDate(date);
			transactionRepository.save(transaction);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}

	@Transactional
	@Override
	public String initiateTransaction(Transaction transaction, User user) {
		try {
    		if(transaction.getToAccount() == null){
                return ERR_ACCOUNT_NOT_EXISTS;
            }
    		transaction.setFromAccount(accountService.getAccountByUser(user));
    		transaction.setStatus(S_PENDING);
    		transaction.setTransferDate(transaction.getTransferDate());
    		transactionRepository.save(transaction);
    		return SUCCESS;
		} catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
	}

    @Override
    public Transaction getTransactionsById(String id) {
        return transactionRepository.findOne(id);
    }

    @Override
    public List<Transaction> getMerchantRequests(String status) {
        List<Transaction> transactions = transactionRepository.findByStatus(status);
        return transactions;
    }

    @Transactional
    @Override
    public String actionOnRequest(String id, String status) {
        try{
            Transaction transaction = transactionRepository.findOne(id);
            transaction.setStatus(status);
            if(status.equals(S_CUSTOMER_VERIFIED)){
            }else{
                
            }
            transactionRepository.save(transaction); 
            return SUCCESS;
        }catch(Exception e){
            return ERROR;
        }
    }
}
