package com.bankapp.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IUserService;

@Controller
public class MainController {
    
    @Autowired
    private IUserService userService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}
	
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        mv.addObject("user", loggedInUser);
        mv.addObject("role", loggedInUser.getRoles().iterator().next().getName());
        mv.setViewName("profile");
        return mv;
    }
	
	@RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView updateProfile(@ModelAttribute("user") User user, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        System.out.println(user.getUsername());
        mv.setViewName("profile");
        return mv;
    }
}
