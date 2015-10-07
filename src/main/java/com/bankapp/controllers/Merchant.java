package com.bankapp.controllers;

import java.util.ArrayList;
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
import com.bankapp.services.IMerchantService;
import com.bankapp.services.IUserService;

@Controller
public class Merchant {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private IMerchantService merchantService;

	@Autowired
	private IUserService userService;

	@RequestMapping(value = "/transaction", method = RequestMethod.GET)
	public ModelAndView getTransactions() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		ModelAndView mv = new ModelAndView();
		transactions = getTransactionsByUserId(1);
		mv.addObject("transactions", transactions);
		mv.setViewName("merchant/transaction");
		return mv;
	}

	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public ModelAndView getAccounts() {
		List<Account> account = new ArrayList<Account>();
		ModelAndView mv = new ModelAndView();
		account = getAccountById(1);
		mv.addObject("accounts", account);
		mv.setViewName("merchant/account");
		return mv;
	}

	private List<Transaction> getTransactionsByUserId(long id) {
		User user = userService.getUserById(id);
		List<Transaction> transactions = merchantService.getTransactionsByUser(user);
		System.out.println(transactions.size());
		return transactions;
	}

	private List<Account> getAccountById(long id) {
		User user = userService.getUserById(id);
		List<Account> account = merchantService.getAccountsByUser(user);
		System.out.println(account.size());
		return account;
	}
}
