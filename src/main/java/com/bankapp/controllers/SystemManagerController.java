/**
 * 
 */
package com.bankapp.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.expression.Lists;

import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IPendingTransaction;
import com.bankapp.services.IUserService;

/**
 * @author Nitesh Dhanpal
 *
 */
@Controller
public class SystemManagerController {
	
	
	
	
	@Autowired
	private IPendingTransaction pending;
	
	
	
	@RequestMapping(value = "/pendingtransaction", method = RequestMethod.GET)
	public ModelAndView getPendingTransaction() {
		List<Transaction> transactions = pending.getTransactionByTflag("Pending");
		ModelAndView mv = new ModelAndView();
		//transactions = pending.getTransactionsByTflag("Pending");
	//	String str = "Pending";
		//transactions = pending.getTransactionsByAmount();
		mv.addObject("pending", transactions);
		mv.setViewName("manager/pendingtransaction");
		return mv;
	}
	
	
	
	

	
}
