package com.bankapp.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
public class SysadminController {

	@Autowired
	IUserService userservice;
	
	@Autowired
	ITransactionService trans;
	
	@RequestMapping(value = "/retrievedetails", method = RequestMethod.POST)
	public ModelAndView RetrieveUserDetails(@ModelAttribute("userid") String userid, @ModelAttribute("users") @Valid User user,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("updatedetails");
		System.out.println(userid);
		User user1 = userservice.getUserById((long)Integer.parseInt(userid));
		//userservice.updateuser((long)Integer.parseInt(userid));
		mv.addObject("userdisplay", user1);
		//mv.setViewName("/SysAd");
		return mv;
	}
	
	@RequestMapping(value = "/change_Username_Manager", method = RequestMethod.GET)
	public ModelAndView username() {
		
		ModelAndView mv = new ModelAndView("sysadmin");
		User user = new User();
				
		mv.addObject("users", user);
		//mv.setViewName("/SysAd");
		return mv;
	}
	
	@RequestMapping(value = "/change_Username_Manager", method = RequestMethod.POST)
	public ModelAndView change_username(@ModelAttribute("username") String username, @ModelAttribute("id") Long id,BindingResult result) {
	  User user = userservice.getUserById(id);
	  
		ModelAndView mv = new ModelAndView("RetrieveManagerDetails");		
		userservice.update_name(username, user);
		
		mv.addObject("userdisplay", "Success");
		//mv.setViewName("/SysAd");
		return mv;
	}
	

	@RequestMapping(value = "/change_Address_Manager", method = RequestMethod.POST)
	public ModelAndView change_Address(@ModelAttribute("useraddress") String useraddress, @ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("RetrieveManagerDetails");
		User user = userservice.getUserById(id);
		userservice.update_Address(useraddress, user);
		
		mv.addObject("userdisplay", "Success");
		//mv.setViewName("/SysAd");
		return mv;
	}

	
	
	@RequestMapping(value = "/change_username", method = RequestMethod.POST)
	public ModelAndView change_name_Employee(@ModelAttribute("username") String username, @ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("RetrieveEmployeeDetails");
		User user = userservice.getUserById(id);				
		userservice.update_name(username, user);
		
		mv.addObject("userdisplay", "Success");
		
		
		return mv;
	}
	

	@RequestMapping(value = "/change_Address", method = RequestMethod.POST)
	public ModelAndView change_Address_Employee(@ModelAttribute("useraddress") String useraddress, @ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("RetrieveEmployeeDetails");
		User user = userservice.getUserById(id);
		userservice.update_Address(useraddress, user);
		
		mv.addObject("userdisplay", "Success");
		
		return mv;
	}


	@RequestMapping(value = "/DeleteUserManager", method = RequestMethod.POST)
	public ModelAndView DeleteUser(@ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("RetrieveManagerDetails");
		User user = userservice.getUserById(id);
		userservice.deleteuser(id);
		
		mv.addObject("userdisplay", "Success");
		
		return mv;
	}
	
	@RequestMapping(value = "/DeleteUserEmployee", method = RequestMethod.POST)
	public ModelAndView DeleteUserEmployee(@ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("RetrieveEmployeeDetails");
		User user = userservice.getUserById(id);
		userservice.deleteuser(id);
		
		mv.addObject("userdisplay", "Success");
		
		return mv;
	}

	
	
	
	
	@RequestMapping(value = "/update_name", method = RequestMethod.POST)
	public ModelAndView updateUserEmail(@ModelAttribute("username") String username, @ModelAttribute("userid") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("updatedetails");
		System.out.println(id);
		User user1 = userservice.getuserbyName(username);
		
		mv.addObject("username_changed", "username changed");
		
		return mv;
	}
	
	
	
	
	@RequestMapping(value = "/sysgetdetails", method = RequestMethod.GET)
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
		User user1 = userservice.getUserById((long)3);
		userservice.adduser(user1);
		
		return mv;
	}
	
	@RequestMapping(value = "/sysadmi", method = RequestMethod.GET)
	public ModelAndView DeleteUserDetails() {
		
		ModelAndView mv = new ModelAndView("sysadmin");
		userservice.deleteuser((long)2);		
		
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
	
	@RequestMapping(value = "/RetrieveManagerDetails", method = RequestMethod.GET)
	public ModelAndView RetrieveManagerDetails() {
		
		ModelAndView mv = new ModelAndView("RetrieveManagerDetails");
		List<User> users = userservice.getManagers();
				
		mv.addObject("info", users);
		
		return mv;
	}
	
	@RequestMapping(value = "/RetrieveEmployeeDetails", method = RequestMethod.GET)
	public ModelAndView RetrieveEmployeeDetails() {
		
		ModelAndView mv = new ModelAndView("RetrieveEmployeeDetails");
		
		List<User> users = userservice.getEmployees();
		mv.addObject("info", users);
		
		return mv;
	}
	
	
	
	
	
}
