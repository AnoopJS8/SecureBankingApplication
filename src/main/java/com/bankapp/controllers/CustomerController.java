package com.bankapp.controllers;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.bankapp.constants.Constants;
import com.bankapp.forms.InitiateTransactionForm;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IAccountService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
@Secured("ROLE_CUSTOMER")
public class CustomerController implements Constants {

    private final Logger LOGGER = Logger.getLogger(CustomerController.class);

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IUserService userService;

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("yyyy/MM/dd"), true);
        binder.registerCustomEditor(Date.class, editor);
    }

    @RequestMapping(value = "/customer/myaccount", method = RequestMethod.GET)
    public ModelAndView getTransactions(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        Account account = accountService.getAccountByUser(loggedInUser);
        List<Transaction> transactions = transactionService.getTransactionsByAccount(account, account);
        mv.addObject("accounts", account);
        mv.addObject("transactions", transactions);
        mv.addObject("role", "customer");
        mv.setViewName("customer/myaccount");
        return mv;
    }

    @RequestMapping(value = "/customer/myprofile", method = RequestMethod.GET)
    public ModelAndView getProfile() {
        ModelAndView mv = new ModelAndView();
        System.out.print("Hello Customer");
        User user = new User();
        mv.addObject("user", user);
        mv.setViewName("customer/myprofile");
        return mv;
    }

    @RequestMapping(value = "/customer/transferfunds", method = RequestMethod.GET)
    public ModelAndView transferFunds() {
        ModelAndView mv = new ModelAndView();
        System.out.print("Hello Customer");
        Transaction transaction = new Transaction();
        mv.addObject("transaction", transaction);
        mv.setViewName("customer/transferfunds");
        return mv;
    }

    @RequestMapping(value = "/customer/transferfunds", method = RequestMethod.POST)
    public ModelAndView saveTransaction(@Valid @ModelAttribute("transaction") Transaction transaction,
            BindingResult result, WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        User user = userService.getUserFromSession(principal);
        String message = transactionService.saveTransaction(transaction, user);

        if (message.equalsIgnoreCase(LESS_BALANCE)) {
            String msg = "You are low on balance, the transaction cannot go through.";
            mv.addObject("message", msg);
            mv.addObject("role", "customer");
            String errorMsg = String.format("Action: %s, Message: %s", "low on balance", msg);
            LOGGER.error(errorMsg);
            mv.setViewName("error");
        } else if (message.equalsIgnoreCase(SUCCESS)) {
            mv.addObject("message", "Money transfered successfully");
            mv.addObject("role", "customer");
            mv.setViewName("success");
        } else if (message.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            mv.addObject("message", ERR_ACCOUNT_NOT_EXISTS);
            mv.setViewName("error");
        } else if (message.equalsIgnoreCase(CRITICAL)) {
            mv.addObject("message", "Its a critical transaction so it will be handled by our employees shortly");
            mv.setViewName("success");
        } else {
            mv.addObject("message", "Invalid Receipent Account Number");
            mv.addObject("role", "customer");
            String errorMsg = String.format("Action: %s, Message: %s", "SQL error", message);
            LOGGER.error(errorMsg);
            mv.setViewName("error");
        }
        return mv;

    }

    @RequestMapping(value = "/customer/initiatetransaction", method = RequestMethod.GET)
    public ModelAndView initiateTransaction() {
        ModelAndView mv = new ModelAndView("customer/initiatetransaction", "form", new InitiateTransactionForm());
        return mv;
    }

    @RequestMapping(value = "/customer/initiatetransaction", method = RequestMethod.POST)
    public ModelAndView initiateTransaction(@ModelAttribute("form") @Valid InitiateTransactionForm form,
            BindingResult result, WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            System.out.println();
            for (ObjectError error : result.getAllErrors()) {
                System.out.println(error);
            }
            mv.setViewName("/customer/initiatetransaction");
            mv.addObject("errors", result.getAllErrors());
            return mv;
        }

        User user = userService.getUserFromSession(principal);

        Transaction transaction = new Transaction();
        Account toAccount = accountService.getAccountByAccountId(form.getAccountId());
        transaction.setAmount(form.getAmount());
        transaction.setToAccount(toAccount);
        transaction.setComment(form.getComment());
        transaction.setTransferDate(form.getTransferDate());

        String message = transactionService.initiateTransaction(transaction, user);
        if (message != null) {
            if (message.equalsIgnoreCase(SUCCESS)) {
                String m = "Your transaction is initiated and will be handled by our employees";
                mv.addObject("message", m);
                mv.addObject("role", "customer");
                String Msg = String.format("Action: %s, Message: %s", "Success", m);
                LOGGER.info(Msg);
                mv.setViewName("success");
            } else {
                mv.addObject("message", "Some error occured");
                mv.addObject("role", "customer");
                String errorMsg = String.format("Action: %s, Message: %s", "Error", message);
                LOGGER.info(errorMsg);
                mv.setViewName("error");
            }
        }
        return mv;

    }}
