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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.expression.Lists;

import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.models.Requests;
import com.bankapp.services.IRegularEmployee;
import com.bankapp.services.IUserService;


@Controller
public class RegularEmployeeController {
	
	
	@Autowired
	private IRegularEmployee pending;
	//private IUserService userservice;
	
	
//	@RequestMapping(value = "/employeependingtransaction", method = RequestMethod.GET)
//	public ModelAndView getPendingTransaction() {
//		List<Transaction> transactions = pending.getTransactionByTflag("Pending");
//		ModelAndView mv = new ModelAndView();
//	
//		mv.addObject("pending", transactions);
//		mv.setViewName("/pendingtransaction");
//		return mv;
//	}
	
	@RequestMapping(value = "/employeerequest", method = RequestMethod.GET)
	public ModelAndView getRequest() {
		List<Requests> user1 = pending.getRequest((long)101);
		//List<Requests> request = userservice.getRequest("Pending");
		System.out.println("users" + user1);
		ModelAndView mv = new ModelAndView();
	
		mv.addObject("greet", user1);
		mv.setViewName("EmployeeRetrieveUserID");
		return mv;
	}
	
	
//	@RequestMapping(value = "/EmployeeRetrieveUserID", method = RequestMethod.POST)
//	public ModelAndView searchUserID() {
//		
//		//List<Transaction> transactions = pending.getTransactionByTflag("Pending");
//		ModelAndView mv = new ModelAndView();
//		
//		mv.addObject("pending", transactions);
//		mv.setViewName("employee/pendingtransaction");
//		return mv;
//	}
	
//	 @RequestMapping(value="/EmployeeRetrieveUserID", method=RequestMethod.GET)
//	    public String greetingForm(Model model) {
//	        model.addAttribute("greeting", new Greeting());
//	        return "greeting";
//	    }
//
//	    @RequestMapping(value="/greeting", method=RequestMethod.POST)
//	    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
//	        model.addAttribute("greeting", greeting);
//	        return "result";
//	    }
	     
	
	
	
	
}
