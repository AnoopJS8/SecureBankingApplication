package com.bankapp.controllers;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.listeners.OnRegistrationCompleteEvent;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;
import com.bankapp.services.IUserService;
import com.bankapp.services.IMailService;

@Controller
public class SignupController {
    private final Logger LOGGER = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IMailService mailService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView getSignupPage() {
        User user = new User();
        ModelAndView modelAndView = new ModelAndView("signup");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView registerUser(@ModelAttribute("user") @Valid User newUser, BindingResult result,
            WebRequest request, Errors errors) {

        LOGGER.debug("Registering user account with information: {}", newUser);

        if (result.hasErrors()) {
            return new ModelAndView("signup", "user", newUser);
        }

        User registered = createUserAccount(newUser);
        if (registered == null) {
            String message = String.format("This email is already taken");
            ModelAndView mv = new ModelAndView("signup");
            mv.addObject("message", message);
            mv.addObject("user", newUser);
            return mv;
        }
        try {
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        } catch (Exception e) {
            String message = String.format("[ERROR]: Action: %s, Message: %s", "signup", e.getMessage());

            LOGGER.debug(message);
            ModelAndView mv = new ModelAndView("signup");
            mv.addObject("message", message);
            mv.addObject("user", newUser);
            return mv;
        }

        ModelAndView mv = new ModelAndView("registration/activationInfo");
        mv.addObject("username", newUser.getUsername());
        mv.addObject("email", newUser.getEmail());
        return mv;
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token) {

        LOGGER.debug("Verifying user account with information: {}", token);
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = String.format("The token is invalid, please register again!");
            return new ModelAndView("registration/activationFailed", "message", message);
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String message = String.format("The verification token has expired. Please register again!");
            String url = "http://localhost:8081/resendRegistrationToken?token=" + token;
            ModelAndView mv = new ModelAndView("registration/activationFailed");
            mv.addObject("message", message);
            mv.addObject("url", url);
            return mv;
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        return new ModelAndView("registration/activationSuccess");
    }

    @RequestMapping(value = "/resendRegistrationToken", method = RequestMethod.GET)
    public ModelAndView resendRegistrationToken(HttpServletRequest request,
            @RequestParam("token") String existingToken) {
        String newToken = userService.generateNewVerificationToken(existingToken).getToken();
        User user = userService.getUser(newToken);

        String recipientAddress = user.getEmail();
        String userName = user.getUsername();
        String subject = String.format("My ASU Bank - Resending Activation");
        String confirmationUrl = "http://localhost:8081/registrationConfirm?token=" + newToken;

        String textBody = String.format(
                "Dear %s, <br /><br />Here is your new account verification link:<br />"
                        + "<a href='%s'>%s</a>.<br /><br />Regards,<br />My ASU Bank",
                userName, confirmationUrl, confirmationUrl);
        mailService.sendEmail(recipientAddress, subject, textBody);

        ModelAndView mv = new ModelAndView("registration/activationInfo");
        mv.addObject("email", recipientAddress);
        mv.addObject("username", userName);
        return mv;
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
