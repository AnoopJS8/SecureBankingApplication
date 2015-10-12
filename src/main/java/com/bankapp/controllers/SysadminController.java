package com.bankapp.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.ITransaction;
import com.bankapp.services.IUserService;

@Controller
public class SysadminController {

	@Autowired
	IUserService userservice;
	
	
	ITransaction trans;
	
	@RequestMapping(value = "/sys", method = RequestMethod.GET)
	public ModelAndView updateUserDetails() {
		
		ModelAndView mv = new ModelAndView("sysadmin");
		userservice.updateuser((long)1);
		mv.addObject("message", "success");
		//mv.setViewName("/SysAd");
		return mv;
	}
	@RequestMapping(value = "/Sys", method = RequestMethod.GET)
	public ModelAndView getUserDetails() {
		
		ModelAndView mv = new ModelAndView("sysadmin");
		User user1 = userservice.getUserById((long)1);
				
		mv.addObject("info", user1);
		//mv.setViewName("/SysAd");
		return mv;
	}
	
	@RequestMapping(value = "/Add", method = RequestMethod.GET)
	public ModelAndView AddUserDetails() {
		
		ModelAndView mv = new ModelAndView("sysadmin");
		User user1 = userservice.getUserById((long)1);
		userservice.adduser(user1);
		
		return mv;
	}
	
	@RequestMapping(value = "/Sy", method = RequestMethod.GET)
	public ModelAndView DeleteUserDetails() {
		
		ModelAndView mv = new ModelAndView("sysadmin");
		userservice.deleteuser((long)1);		
		
		mv.addObject("delete", "Success");
		
		return mv;
	}
	
	@RequestMapping(value = "/PII", method = RequestMethod.GET)
	public ModelAndView GetPIIUsers() {
		
		ModelAndView mv = new ModelAndView("sysadmin");
		User user = userservice.getUserById((long)1);		
		String s = userservice.getPII(user);
		mv.addObject("PII", s);
		
		return mv;
	}
	
	@RequestMapping(value = "/transverify", method = RequestMethod.GET)
	public ModelAndView VerifyInternalRequests(Transaction t) {
		
		ModelAndView mv = new ModelAndView("sysadmin");
		String getInfo = trans.TransactionVerifyDetails(t);
		mv.addObject("Verify", getInfo );
		//mv.setViewName("/SysAd");
		return mv;
	}
	
	
	
}
