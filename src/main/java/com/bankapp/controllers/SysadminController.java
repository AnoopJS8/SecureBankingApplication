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
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
public class SysadminController {

	@Autowired
	IUserService userservice;
	
	@Autowired
	ITransactionService trans;
	
	@Autowired
	IProfileRequestService profileService;
	
	@RequestMapping(value = "/retrievedetails", method = RequestMethod.POST)
	public ModelAndView RetrieveUserDetails(@ModelAttribute("userid") String userid, @ModelAttribute("users") @Valid User user,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/updatedetails");
		System.out.println(userid);
		User user1 = userservice.getUserById((long)Integer.parseInt(userid));
		//userservice.updateuser((long)Integer.parseInt(userid));
		mv.addObject("userdisplay", user1);
		//mv.setViewName("/SysAd");
		return mv;
	}
	
	@RequestMapping(value = "/change_Username_Manager", method = RequestMethod.GET)
	public ModelAndView username() {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/sysadmin");
		User user = new User();
				
		mv.addObject("users", user);
		//mv.setViewName("/SysAd");
		return mv;
	}
	
	@RequestMapping(value = "/sysadmin", method = RequestMethod.GET)
	public ModelAndView AdminDetails() {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/sysadmin");
		
				
		
		return mv;
	}
	
	@RequestMapping(value = "/change_Username_Manager", method = RequestMethod.POST)
	public ModelAndView change_username(@ModelAttribute("username") String username, @ModelAttribute("id") Long id,BindingResult result) {
	  User user = userservice.getUserById(id);
	  
		ModelAndView mv = new ModelAndView("/systemAdmin/RetrieveManagerDetails");		
		userservice.update_name(username, user);
		
		mv.addObject("userdisplay", "Success");
		//mv.setViewName("/SysAd");
		return mv;
	}
	

	@RequestMapping(value = "/change_Address_Manager", method = RequestMethod.POST)
	public ModelAndView change_Address(@ModelAttribute("useraddress") String useraddress, @ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/RetrieveManagerDetails");
		User user = userservice.getUserById(id);
		userservice.update_Address(useraddress, user);
		
		mv.addObject("userdisplay", "Success");
		//mv.setViewName("/SysAd");
		return mv;
	}

	
	
	@RequestMapping(value = "/change_username", method = RequestMethod.POST)
	public ModelAndView change_name_Employee(@ModelAttribute("username") String username, @ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/RetrieveEmployeeDetails");
		User user = userservice.getUserById(id);				
		userservice.update_name(username, user);
		
		mv.addObject("userdisplay", "Success");
		
		
		return mv;
	}
	

	@RequestMapping(value = "/change_Address", method = RequestMethod.POST)
	public ModelAndView change_Address_Employee(@ModelAttribute("useraddress") String useraddress, @ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/RetrieveEmployeeDetails");
		User user = userservice.getUserById(id);
		userservice.update_Address(useraddress, user);
		
		mv.addObject("userdisplay", "Success");
		
		return mv;
	}


	@RequestMapping(value = "/DeleteUserManager", method = RequestMethod.POST)
	public ModelAndView DeleteUser(@ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/RetrieveManagerDetails");
		User user = userservice.getUserById(id);
		userservice.deleteuser(id);
		
		mv.addObject("userdisplay", "Success");
		
		return mv;
	}
	
	@RequestMapping(value = "/DeleteUserEmployee", method = RequestMethod.POST)
	public ModelAndView DeleteUserEmployee(@ModelAttribute("id") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/RetrieveEmployeeDetails");
		User user = userservice.getUserById(id);
		userservice.deleteuser(id);
		
		mv.addObject("userdisplay", "Success");
		
		return mv;
	}

	
	
	
	
	@RequestMapping(value = "/update_name", method = RequestMethod.POST)
	public ModelAndView updateUserEmail(@ModelAttribute("username") String username, @ModelAttribute("userid") Long id,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/updatedetails");
		System.out.println(id);
		User user1 = userservice.getuserbyName(username);
		
		mv.addObject("username_changed", "username changed");
		
		return mv;
	}
	
	
	
	@RequestMapping(value = "/RetrieveManagerDetails", method = RequestMethod.GET)
	public ModelAndView RetrieveManagerDetails() {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/RetrieveManagerDetails");
		List<User> users = userservice.getManagers();
				
		mv.addObject("info", users);
		
		return mv;
	}
	
	@RequestMapping(value = "/RetrieveEmployeeDetails", method = RequestMethod.GET)
	public ModelAndView RetrieveEmployeeDetails() {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/RetrieveEmployeeDetails");
		
		List<User> users = userservice.getEmployees();
		mv.addObject("info", users);
		
		return mv;
	}
	
	@RequestMapping(value = "/profileRequest", method = RequestMethod.GET)
	public ModelAndView profileRequest() {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/profilerequest");
		List<ProfileRequest> request = profileService.findStatus("pending");
				
		mv.addObject("request", request);
		
		return mv;
	}
	
	@RequestMapping(value = "/changeRequest", method = RequestMethod.POST)
	public ModelAndView ChangeRequest(@ModelAttribute("rId") Long rid,BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/systemAdmin/profilerequest");
		profileService.changerequest(rid);
		
		
		mv.addObject("userdisplay", "Success");
		
		return mv;
	}

	
	
}
