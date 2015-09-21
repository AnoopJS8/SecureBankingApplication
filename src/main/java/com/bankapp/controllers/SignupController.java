package com.bankapp.controllers;


import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserAlreadyExistException;
import com.bankapp.models.User;
import com.bankapp.services.IUserService;

@Controller
public class SignupController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @RequestMapping(value="/signup",  method=RequestMethod.GET)
    public ModelAndView getSignupPage() {
        User user = new User();
        ModelAndView modelAndView = new ModelAndView("signup");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @RequestMapping(value="/signup", method=RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") @Valid User newUser) {
        LOGGER.debug("Registering user account with information: {}", newUser);

        final User registered = createUserAccount(newUser);
        if (registered == null) {
            throw new UserAlreadyExistException();
        }

        return "redirect:/";
    }

    private User createUserAccount(final User newUser) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(newUser);
        } catch (final EmailExistsException e) {
            return null;
        }
        return registered;
    }
}

