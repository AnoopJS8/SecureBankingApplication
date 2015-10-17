package com.bankapp.controllers;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.listeners.OnRegistrationCompleteEvent;
import com.bankapp.models.Account;
import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;
import com.bankapp.services.IUserService;
import com.bankapp.services.IAccountService;
import com.bankapp.services.IMailService;

@Controller
public class SignupController {
    private final Logger LOGGER = Logger.getLogger(SignupController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IMailService mailService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Value("${com.bankapp.account.default_balance}")
    private double defaultBalance;

    @Value("${com.bankapp.account.default_critical_limit}")
    private double defaultCriticalLimit;

    final private String signupViewName = "registration/signup";

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView getSignupPage() {
        User user = new User();
        Role role = new Role();
        ModelAndView modelAndView = new ModelAndView(signupViewName);
        modelAndView.addObject("user", user);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView registerUser(@Valid @ModelAttribute("user") User newUser, BindingResult resultUser,
            @ModelAttribute("role") Role role, BindingResult resultRole, HttpServletRequest request) {

        ModelAndView mv = new ModelAndView(signupViewName);

        if (resultUser.hasErrors()) {
            mv.addObject("user", newUser);
            mv.addObject("errors", resultUser.getAllErrors());
            return mv;
        }

        String logMessage = String.format("Registering user account with information: {%s, %s}", newUser, role);
        LOGGER.info(logMessage);

        User registered = createUserAccount(newUser, role.getName());
        if (registered == null) {
            String message = String.format("This email is already taken");
            mv.addObject("message", message);
            mv.addObject("user", newUser);
            return mv;
        }
        try {
            eventPublisher
                    .publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        } catch (Exception e) {
            String message = String.format("Action: %s, Message: %s", "signup", e.getMessage());
            LOGGER.error(message);

            mv.addObject("message", e.getMessage());
            mv.addObject("user", newUser);
            return mv;
        }

        mv.setViewName("registration/activationInfo");
        mv.addObject("username", newUser.getUsername());
        mv.addObject("email", newUser.getEmail());
        return mv;
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(HttpServletRequest request, Model model,
            @RequestParam("token") String token) {

        String logMessage = String.format("Verifying user account with information: {token = %s}", token);
        LOGGER.info(logMessage);

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = String.format("The token is invalid, please register again!");
            return new ModelAndView("registration/activationFailed", "message", message);
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String message = String.format("The verification token has expired. Please register again!");
            String url = getAppUrl(request) + "/resendRegistrationToken?token=" + token;
            ModelAndView mv = new ModelAndView("registration/activationFailed");
            mv.addObject("message", message);
            mv.addObject("url", url);
            return mv;
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);

        // Create user account
        Account userAccount = new Account(user, defaultBalance, defaultCriticalLimit);
        accountService.saveAccount(userAccount);
        logMessage = String.format("User %s has been verified, created new account [%s]", user.getId(), userAccount);
        LOGGER.info(logMessage);

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
        String confirmationUrl = getAppUrl(request) + "/registrationConfirm?token=" + newToken;

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

    private User createUserAccount(final User newUser, final String roleName) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(newUser, roleName);
        } catch (final EmailExistsException e) {
            return null;
        }
        return registered;
    }

    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
