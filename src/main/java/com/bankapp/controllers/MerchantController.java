package com.bankapp.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.constants.Constants;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IAccountService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;


@Controller
public class MerchantController implements Constants {

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
		Account account = getAccountById(userService.getUserByEmail(getPrincipal()).getId());
		List<Transaction> transactions = getTransactionsByAccount(account);
		mv.addObject("accounts", account);
		mv.addObject("transactions", transactions);
		mv.setViewName("merchant/myaccount");
		return mv;
	}
	
	@RequestMapping(value = "/transferfunds", method = RequestMethod.GET)
	public ModelAndView transferFunds() {
		ModelAndView mv = new ModelAndView();
		Transaction transaction = new Transaction();
		mv.addObject("transaction", transaction);
		mv.setViewName("merchant/transferfunds");
		return mv;
	}
	
	@RequestMapping(value="/transferfunds", method = RequestMethod.POST)
	public ModelAndView saveTransaction(@ModelAttribute("transaction") Transaction transaction, BindingResult result,
			WebRequest request, Errors errors) {
		ModelAndView mv = new ModelAndView();
		User user = userService.getUserByEmail(getPrincipal());
		String message = transactionService.saveTransaction(transaction, user);
		if(message!=null){
			if(message.equalsIgnoreCase(LESS_BALANCE)){
				mv.addObject("message", "You are low on balance, the transaction cannot go through.");
				mv.setViewName("success");
			}else if(message.equalsIgnoreCase(SUCCESS)){
				mv.addObject("message", "Money transfered successfully");
				mv.setViewName("success");
			}else{
				mv.addObject("message", "Error");
				mv.setViewName("success");
			}
			
		}else{
			mv.addObject("message", "Error");
			mv.setViewName("success");
		}
		return mv;
		
	}

	private Account getAccountById(long id) {
		User user = userService.getUserById(id);
		Account account = accountService.getAccountsByUser(user);
		return account;
	}
	
	private List<Transaction> getTransactionsByAccount(Account account) {
		List<Transaction> transactions = transactionService.getTransactionsByAccount( account, account);
		System.out.println(transactions.size());
		return transactions;
	}
	
	 private String getPrincipal(){
	        String userName = null;
	        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	 
	        if (principal instanceof UserDetails) {
	            userName = ((UserDetails)principal).getUsername();
	        } else {
	            userName = principal.toString();
	        }
	        return userName;
	  }
}
