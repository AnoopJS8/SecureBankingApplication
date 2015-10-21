package com.bankapp.controllers;


import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.models.User;
import com.bankapp.constants.Constants;
import com.bankapp.models.ProfileRequest;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.IUserService;

@Controller
public class MainController implements Constants{
    
    private final Logger LOGGER = Logger.getLogger(MainController.class);

    @Autowired
    private IUserService userService;
    
    @Autowired
    private IProfileRequestService profileRequestService;
    
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}

    @RequestMapping(value = "/login/identify", method = RequestMethod.GET)
    public ModelAndView getRecoverPassword() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("registration/recoverPassword");
        mv.addObject("email", "");
        return mv;
    }

    @RequestMapping(value = "/login/identify", method = RequestMethod.POST)
    public ModelAndView postRecoverPassword(@ModelAttribute("email") String email) {
        String logMessage = String.format("Password recovery request for user [email=%s]", email);
        LOGGER.info(logMessage);

        User registeredUser = userService.getUserByEmail(email);
        ModelAndView mv = new ModelAndView();

        if (registeredUser == null) {
            String message = String.format("Sorry, we could not find any user with the email '%s'", email);
            mv.setViewName("error");
            mv.addObject("message", message);
            return mv;
        } else {
            mv.setViewName("registration/verifySecurityQuestion");
            mv.addObject("email", email);
            mv.addObject("securityQuestion", registeredUser.getSecurityQuestion());
            mv.addObject("securityAnswer", "");
            return mv;
        }
    }

    @RequestMapping(value = "/login/verifyIdentity", method = RequestMethod.POST)
    public ModelAndView verifySecurityAnswer(@ModelAttribute("email") String email, BindingResult bindingEmail,
            @ModelAttribute("securityQuestion") String question, BindingResult bindingQuestionResult,
            @ModelAttribute("securityAnswer") String answer, BindingResult bindingAnswerResult) {
        String logMessage = String.format("Verifying user email %s with [Question: %s, Answer: %s]", email, question,
                answer);
        LOGGER.info(logMessage);

        User registeredUser = userService.getUserByEmail(email);
        ModelAndView mv = new ModelAndView();

        if (registeredUser == null) {
            String message = String.format("Sorry, we could not find any user with the email '%s'", email);
            mv.setViewName("error");
            mv.addObject("message", message);
            return mv;
        } else {
            String expectedAnswer = registeredUser.getSecurityAnswer();
            if(expectedAnswer.equalsIgnoreCase(answer)) {
                String message = String.format(
                        "Thank you for answering your security question. We have sent an email with a temporary password to %s",
                        email);
                mv.setViewName("success");
                mv.addObject("message", message);
                userService.generateTemporaryPassword(registeredUser);
                return mv;
            } else {
                String message = String.format("Sorry, we could not verify the answer you specified. Please try again.");
                mv.setViewName("error");
                mv.addObject("message", message);
                return mv;
            }
        }
    }
    
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        mv.addObject("user", loggedInUser);
        mv.addObject("role", loggedInUser.getRole().getName());
        mv.setViewName("profile");
        return mv;
    }
    
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView updateProfile(@ModelAttribute("user") @Valid User user, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        ProfileRequest profile = new ProfileRequest();
        profile.setAddress(user.getAddress());
        profile.setDateOfBirth(user.getDateOfBirth());
        profile.setPhoneNumber(user.getPhoneNumber());
        profile.setStatus(S_PROFILE_UPDATE_PENDING);
        profile.setUser(userService.getUserFromSession(principal));
        String message = profileRequestService.saveProfileRequest(profile);
        if(message.equalsIgnoreCase(ERROR)){
            mv.addObject("message", "Error occured");
            mv.setViewName("error");
            return mv;
        }
        mv.addObject("message", "Request for changes are sent to out employee");
        mv.setViewName("success");
        return mv;
    }
}
