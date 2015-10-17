/**
 * 
 */
package com.bankapp.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.expression.Lists;

import com.bankapp.exceptions.EmailDoesNotExist;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserAlreadyExistException;
import com.bankapp.exceptions.UserIdDoesNotExist;
import com.bankapp.exceptions.UserNameExistsException;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.IUserService;
import com.bankapp.constants.Constants;;;

/**
 * @author Nitesh Dhanpal
 *
 */
@Controller
public class SystemManagerController implements Constants {

    @Autowired
    private ISystemManagerService manager;

    private final Logger LOGGER = Logger.getLogger(SystemManagerController.class);

    @RequestMapping(value = "/criticaltransaction", method = RequestMethod.GET)
    public ModelAndView getPendingTransaction() {
        List<Transaction> transactions = manager.getTransactionsByStatus(S_OTP_VERIFIED);
        ModelAndView mv = new ModelAndView();
        mv.addObject("critical", transactions);
        mv.setViewName("manager/criticaltransactions");
        return mv;
    }

    @RequestMapping(value = "/approvetransaction", method = RequestMethod.POST)
    public ModelAndView approvetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        System.out.println("Entered Approve");
        System.out.println("Transaction" + Id.getTransactionId());

        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());

        Account FromAccount = transaction.getFromAccount();
        Account ToAccount = transaction.getToAccount();
        Double AmountToBeSent = transaction.getAmount();
        System.out.println(AmountToBeSent);

        Double FromAccountBalance = FromAccount.getBalance();
        System.out.println(FromAccountBalance);

        String str = "";

        if (FromAccountBalance > AmountToBeSent) {
            manager.reflectChangesToSender(FromAccount, FromAccountBalance, AmountToBeSent);
            Double ToAccountBalance = ToAccount.getBalance();
            manager.reflectChangesToReceiver(ToAccount, ToAccountBalance, AmountToBeSent);
            str = manager.approveTransaction(transaction);
        } else {
            str = "Unsuccessfull";
        }

        mv.addObject("result1", str);
        System.out.println("Done");
        mv.setViewName("manager/criticaltransactions");

        return mv;
    }

    @RequestMapping(value = "/manager_adduser", method = RequestMethod.POST)
    public ModelAndView addUser(User user_request) {
        User user = null;

        try {
            user = manager.addUser(user_request);
        } catch (EmailExistsException e) {
            String message = String.format("Action: %s, Message: %s", "email_exists", e.getMessage());
            LOGGER.error(message);
            return null;
        } catch (UserNameExistsException e) {
            String message = String.format("Action: %s, Message: %s", "username_exists", e.getMessage());
            LOGGER.error(message);
            return null;
        } catch (UserAlreadyExistException e) {
            String message = String.format("Action: %s, Message: %s", "user_exists", e.getMessage());
            LOGGER.error(message);
            return null;
        }

        ModelAndView mv = new ModelAndView();
        mv.addObject("manager_ceateuser", user);
        mv.setViewName("manager/manager_view");

        return mv;

    }

    @RequestMapping(value = "/manager_viewuser_byemail", method = RequestMethod.GET)
    public ModelAndView getuser_byemail(String email) {

        User user = null;

        try {
            user = manager.viewUserByEmail(email);
        } catch (EmailDoesNotExist e) {
            String message = String.format("Action: %s, Message: %s", "EmailDoesNotExist", e.getMessage());
            LOGGER.error(message);
            return null;
        }

        ModelAndView mv = new ModelAndView();
        mv.addObject("viewuser", user);
        mv.setViewName("manager/manager_view");
        return mv;
    }

    @RequestMapping(value = "/manager_viewuser_byid", method = RequestMethod.GET)
    public ModelAndView getuser_byid(Long id) {

        User user = null;

        try {
            user = manager.viewUserById(id);
        } catch (UserIdDoesNotExist e) {
            String message = String.format("Action: %s, Message: %s", "EmailDoesNotExist", e.getMessage());
            LOGGER.error(message);
            return null;
        }

        ModelAndView mv = new ModelAndView();
        mv.addObject("viewuser", user);
        mv.setViewName("manager/manager_view");
        return mv;
    }

}
