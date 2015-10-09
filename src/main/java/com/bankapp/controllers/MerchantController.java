package com.bankapp.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IAccountService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
public class MerchantController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private ITransactionService transactionService;

	@Autowired
	private IUserService userService;


	@RequestMapping(value = "/myaccount", method = RequestMethod.GET)
	public ModelAndView getTransactions() {
		ModelAndView mv = new ModelAndView();
		Account account = getAccountById(1);
		List<Transaction> transactions = getTransactionsByUserId(1, account);
		mv.addObject("accounts", account);
		mv.addObject("transactions", transactions);
		mv.setViewName("merchant/myaccount");
		return mv;
	}

	private Account getAccountById(long id) {
		User user = userService.getUserById(id);
		Account account = accountService.getAccountsByUser(user);
		return account;
	}
	
	private List<Transaction> getTransactionsByUserId(long id, Account account) {
		User user = userService.getUserById(id);
		List<Transaction> transactions = transactionService.getTransactionsByUserAndAccount(user, account, account);
		System.out.println(transactions.size());
		return transactions;
	}
}
