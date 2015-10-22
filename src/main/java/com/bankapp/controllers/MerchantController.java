package com.bankapp.controllers;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
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
@Secured("ROLE_MERCHANT")
public class MerchantController implements Constants {

    private final Logger LOGGER = Logger.getLogger(MerchantController.class);

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/merchant/myaccount", method = RequestMethod.GET)
    public ModelAndView getTransactions(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        Account account = getAccountByUserId(loggedInUser.getId());
        List<Transaction> transactions = getTransactionsByAccount(account);
        mv.addObject("accounts", account);
        mv.addObject("transactions", transactions);
        mv.addObject("role", "merchant");
        mv.setViewName("merchant/myaccount");
        return mv;
    }

    @RequestMapping(value = "/merchant/transferfunds", method = RequestMethod.GET)
    public ModelAndView transferFunds() {
        ModelAndView mv = new ModelAndView();
        Transaction transaction = new Transaction();
        mv.addObject("transaction", transaction);
        mv.addObject("role", "merchant");
        mv.setViewName("merchant/transferfunds");
        return mv;
    }

    @RequestMapping(value = "/merchant/transferfunds", method = RequestMethod.POST)
    public ModelAndView saveTransaction(@ModelAttribute("transaction") @Valid Transaction transaction, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("role", "merchant");
        User user = userService.getUserFromSession(principal);
        String message = transactionService.saveTransaction(transaction, user);
        if (message.equalsIgnoreCase(LESS_BALANCE)) {
            String msg = "You are low on balance, the transaction cannot go through.";
            mv.addObject("message", msg);
            String errorMsg = String.format("Action: %s, Message: %s", "save transaction", msg);
            LOGGER.error(errorMsg);
            mv.setViewName("error");
        } else if (message.equalsIgnoreCase(SUCCESS)) {
            mv.addObject("message", "Money transfered successfully");
            mv.setViewName("success");
        } else if (message.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            mv.addObject("message", ERR_ACCOUNT_NOT_EXISTS);
            mv.setViewName("error");
        } else if (message.equalsIgnoreCase(CRITICAL)) {
            mv.addObject("message", "Its a critical transaction so it will be handled by our employees shortly");
            mv.setViewName("success");
        }else {
            mv.addObject("message", "Error");
            String errorMsg = String.format("Action: %s, Message: %s", "Error", message);
            LOGGER.error(errorMsg);
            mv.setViewName("error");
        }

        return mv;

    }

    @RequestMapping(value = "/merchant/userpayment", method = RequestMethod.GET)
    public ModelAndView askUserPayment() {
        ModelAndView mv = new ModelAndView();
        Transaction transaction = new Transaction();
        mv.addObject("transaction", transaction);
        mv.addObject("role", "merchant");
        mv.setViewName("merchant/userpayment");
        return mv;
    }

    @RequestMapping(value = "/merchant/userpayment", method = RequestMethod.POST)
    public ModelAndView askUserPayment(@ModelAttribute("transaction") @Valid Transaction transaction, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("role", "merchant");
        User user = userService.getUserFromSession(principal);
        String message = transactionService.askCustomerPayment(transaction, user);
        if (message.equalsIgnoreCase(SUCCESS)) {
            mv.addObject("message",
                    "Notified the user for approval and transaction is in pending state till customers approval");
            String Msg = String.format("Action: %s, Message: %s", "Success",
                    "Notified the user for approval and transaction is in pending state till customers approval");
            LOGGER.error(Msg);
            mv.setViewName("success");
        } else {
            mv.addObject("message", "Some error occured");
            String errorMsg = String.format("Action: %s, Message: %s", "Error", message);
            LOGGER.error(errorMsg);
            mv.setViewName("success");
        }

        return mv;

    }

    @RequestMapping(value = "/merchant/initiatetransaction", method = RequestMethod.GET)
    public ModelAndView initiateTransaction() {
        ModelAndView mv = new ModelAndView("merchant/initiatetransaction", "form", new InitiateTransactionForm());
        return mv;
    }

    @RequestMapping(value = "/merchant/initiatetransaction", method = RequestMethod.POST)
    public ModelAndView initiateTransaction(@ModelAttribute("form") @Valid InitiateTransactionForm form,
            BindingResult result, WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        if (result.hasErrors()) {
            System.out.println();
            for (ObjectError error : result.getAllErrors()) {
                System.out.println(error);
            }
            mv.setViewName("/merchant/initiatetransaction");
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
        mv.addObject("role", "merchant");
        String message = transactionService.initiateTransaction(transaction, user);
        if (message.equalsIgnoreCase(SUCCESS)) {
            String m = "Your transaction is initiated and will be handled by our employees";
            mv.addObject("message", m);
            String Msg = String.format("Action: %s, Message: %s", "Success", m);
            LOGGER.error(Msg);
            mv.setViewName("success");
        } else {
            mv.addObject("message", "Some error occured");
            String errorMsg = String.format("Action: %s, Message: %s", "Error", message);
            LOGGER.error(errorMsg);
            mv.setViewName("error");
        }

        return mv;

    }

    private Account getAccountByUserId(long id) {
        User user = userService.getUserById(id);
        Account account = accountService.getAccountByUser(user);
        return account;
    }

    private List<Transaction> getTransactionsByAccount(Account account) {
        List<Transaction> transactions = transactionService.getTransactionsByAccount(account, account);
        System.out.println(transactions.size());
        return transactions;
    }

}
