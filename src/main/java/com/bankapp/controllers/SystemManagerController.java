/**
 * 
 */
package com.bankapp.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserNameExistsException;
import com.bankapp.forms.AddEmployeeForm;
import com.bankapp.forms.ViewByEmailForm;
import com.bankapp.listeners.OnRegistrationCompleteEvent;
import com.bankapp.models.Account;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.Role;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.IUserService;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;;;



/**
 * @author Nitesh Dhanpal
 *
 */



@Controller
@Secured("ROLE_MANAGER")
public class SystemManagerController implements Constants {

	@Autowired
    private ISystemManagerService manager;

    @Autowired
    private IUserService user_service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationEventPublisher eventPublisher;
		
	 @RequestMapping(value = "/manager/deleteUsers", method = RequestMethod.GET)
	     public ModelAndView deleteUser() {
	     
		 ModelAndView mv = new ModelAndView();
		 mv.setViewName("/manager/deleteUser");
		 List<User> users = user_service.displayDeleteUsers();
		 mv.addObject("viewuser", users);
		 return mv;
	    }
	 
	 @RequestMapping(value = "/manager/deleteUsers", method = RequestMethod.POST)
	    public String deleteManager(@ModelAttribute("user") User user,
	            BindingResult result, RedirectAttributes attributes) {
		 	Message message;
	        user_service.deleteExternalUser(user);
	        String msg = String.format("Manager '%s' has been deleted",
	                user.getUsername());
	        message = new Message("succes", msg);
	        attributes.addFlashAttribute("message", message);
	        return "redirect:/manager/deleteUsers";
	    }



    

    private final Logger LOGGER = Logger.getLogger(SystemManagerController.class);

    @RequestMapping(value = "/manager/criticaltransaction", method = RequestMethod.GET)
    public ModelAndView getCriticalTransaction() {
        List<Transaction> transactions = manager.getTransactionsByStatus(S_OTP_VERIFIED);
        ModelAndView mv = new ModelAndView();
        mv.addObject("critical", transactions);
        mv.setViewName("/manager/viewTransaction");
        return mv;
    }

    @RequestMapping(value = "/manager/pendingtransaction", method = RequestMethod.GET)
    public ModelAndView getInitiatedTransaction() {
        List<Transaction> transactions = manager.getTransactionsByStatus(S_PENDING);
        ModelAndView mv = new ModelAndView();
        mv.addObject("pending", transactions);
        mv.setViewName("/manager/viewPending");
        return mv;
    }

    @RequestMapping(value = "/manager/getUserByEmail", method = RequestMethod.GET)
    public ModelAndView getuserEmail() {
        ModelAndView mv = new ModelAndView("/manager/viewUserByEmailForm", "form", new ViewByEmailForm());
        return mv;
    }

    @RequestMapping(value = "/manager/myaccount", method = RequestMethod.GET)
    public ModelAndView getmanagerhome(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = user_service.getUserFromSession(principal);
        String Username = loggedInUser.getUsername();
        mv.addObject("username", Username);
        mv.setViewName("manager/myaccount");
        return mv;
    }

    @RequestMapping(value = "/manager/approvetransaction", method = RequestMethod.POST)
    public ModelAndView approvetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        // //System.out.println("Entered Approve");
        // //System.out.println("Transaction" + Id.getTransactionId());

        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());

        Account FromAccount = transaction.getFromAccount();
        Account ToAccount = transaction.getToAccount();
        Double AmountToBeSent = transaction.getAmount();
        // System.out.println(AmountToBeSent);

        Double FromAccountBalance = FromAccount.getBalance();
        // System.out.println(FromAccountBalance);

        String str = "";

        if (FromAccountBalance > AmountToBeSent) {
            manager.reflectChangesToSender(FromAccount, FromAccountBalance, AmountToBeSent);
            Double ToAccountBalance = ToAccount.getBalance();
            manager.reflectChangesToReceiver(ToAccount, ToAccountBalance, AmountToBeSent);
            str = manager.approveTransaction(transaction);
        } else {
            str = "Unsuccessfull";
        }
        // System.out.println(str);
        mv.addObject("result1", str);
        // System.out.println("Done");
        mv.setViewName("/manager/viewTransaction");

        return mv;
    }

    @RequestMapping(value = "/manager/addUserForm", method = RequestMethod.GET)
    public ModelAndView getUserAddage() {
        ModelAndView modelAndView = new ModelAndView("/manager/addUserForm", "form", new AddEmployeeForm());
        return modelAndView;
    }

    @RequestMapping(value = "/manager/addUserForm", method = RequestMethod.POST)
    public String addUser(final ModelMap model, @ModelAttribute("form") @Valid AddEmployeeForm form,
            BindingResult result, Errors errors, Principal principal, HttpServletRequest request,
            RedirectAttributes attributes) throws UserNameExistsException {
        User user = new User();
        Role role = form.getRole();
        String redirectUrl = "redirect:/manager/addUserForm";
        String status = "success";

        System.out.println(form.getUser().getUsername());
        if (result.hasErrors()) {
            model.addAttribute("form", form);
            System.out.println("asd");
            return "/manager/addUserForm";
        }

        User registered = null;

        user.setEmail(form.getUser().getEmail());
        user.setUsername(form.getUser().getUsername());

        String temporaryPassword = OneTimePassword.generateOTP();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        String message = "Success";
        try {
            registered = user_service.registerNewUserAccount(user, role.getName());
        } catch (EmailExistsException e1) {
            status = "error";
            message = String.format("Action: %s, Message: %s", "signup", e1.getMessage());
            LOGGER.error(message);
            redirectUrl = "redirect:/manager/addUserForm";

            System.out.println("message" + message);
        }

        if (registered != null) {
            try {

                eventPublisher.publishEvent(
                        new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
            } catch (Exception e) {
                status = "error";
                message = String.format("Action: %s, Message: %s", "signup", e.getMessage());
                LOGGER.error(message);

            }

            user_service.generateTemporaryPassword(registered);
        }

        System.out.println("message out" + message);
        attributes.addFlashAttribute("message", new Message(status, message));
        return redirectUrl;

    }

    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    @RequestMapping(value = "/manager/viewUserByEmailForm", method = RequestMethod.POST)
    public ModelAndView getuser_byemail(final ModelAndView model, @ModelAttribute("form") @Valid ViewByEmailForm form,
            BindingResult result, Errors errors, Principal principal, RedirectAttributes attributes) {

        User user = null;
        String status = "success";
        String message = "";

        if (result.hasErrors()) {
            model.addObject("form", form);
            model.setViewName("/manager/viewUserByEmailForm");

            return model;
        }

        if (user_service.emailExist(form.getEmail())) {
            try {
                user = manager.viewUserByEmail(form.getEmail());
            } catch (Exception e) {
                status = "error";
                message = String.format("Message: %s", e.getMessage());
                LOGGER.error(message);
            }

        } else {
            status = "error";
            message = String.format("Action: EmailDoesNotExist");
            // System.out.println(message);
            model.addObject("message", new Message(status, message));
            model.setViewName("/manager/viewUserByEmailForm");
            return model;

        }

        model.addObject("viewuser", user);
        model.setViewName("/manager/viewUser");

        return model;

    }

}

