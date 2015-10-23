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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.forms.InitiateTransactionForm;
import com.bankapp.forms.TransferFundsForm;
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
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("MM/dd/yyyy"), true);
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

        String message = String.format("[Action=%s, Method=%s][User=%s, Account=%s, NumberOfTransactions=%s]",
                "myaccount", "GET", loggedInUser.getEmail(), account.getAccId(), transactions.size());
        LOGGER.info(message);

        return mv;
    }

    @RequestMapping(value = "/customer/transferfunds", method = RequestMethod.GET)
    public ModelAndView transferFunds() {

        ModelAndView mv = new ModelAndView("customer/transferfunds", "form", new TransferFundsForm());

        String logMessage = String.format("[Action=%s, Method=%s]", "transferfunds", "GET");
        LOGGER.info(logMessage);

        return mv;
    }

    @RequestMapping(value = "/customer/transferfunds", method = RequestMethod.POST)
    public String saveTransaction(final ModelMap model, @ModelAttribute("form") @Valid TransferFundsForm form,
            BindingResult result, WebRequest request, Principal principal, RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl;
        String logMessage;

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "customer/transferfunds";
        }

        User user = userService.getUserFromSession(principal);
        Transaction transactionF = new Transaction();
        Account toAccount = accountService.getAccountByAccountId(form.getAccountId());
        transactionF.setAmount(form.getAmount());
        transactionF.setToAccount(toAccount);
        transactionF.setComment(form.getComment());

        String dbStatus = transactionService.saveTransaction(transactionF, user);

        if (dbStatus.equalsIgnoreCase(LESS_BALANCE)) {
            status = "error";
            message = "You are low on balance, the transaction cannot go through.";
            redirectUrl = "redirect:/customer/transferfunds";
        } else if (dbStatus.equalsIgnoreCase(SUCCESS)) {
            status = "success";
            message = "Money transfered successfully";
            redirectUrl = "redirect:/customer/myaccount";
        } else if (dbStatus.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            status = "error";
            message = ERR_ACCOUNT_NOT_EXISTS;
            redirectUrl = "redirect:/customer/transferfunds";
        } else if (dbStatus.equalsIgnoreCase(CRITICAL)) {
            status = "success";
            message = "Its a critical transaction, so it will be handled by our employees shortly";
            redirectUrl = "redirect:/customer/myaccount";
        } else {
            status = "error";
            message = "An unhandled error occurred. Please contact the administrator";
            redirectUrl = "redirect:/customer/transferfunds";
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", "customer");

        logMessage = String.format("[Action=%s, Method=%s][Status=%s][Message=%s]", "transferfunds", "POST", dbStatus,
                message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }

    @RequestMapping(value = "/customer/initiatetransaction", method = RequestMethod.GET)
    public ModelAndView initiateTransaction() {

        ModelAndView mv = new ModelAndView("customer/initiatetransaction", "form", new InitiateTransactionForm());

        String logMessage = String.format("[Action=%s, Method=%s]", "initiatetransaction", "GET");
        LOGGER.info(logMessage);

        return mv;
    }

    @RequestMapping(value = "/customer/initiatetransaction", method = RequestMethod.POST)
    public String initiateTransaction(final ModelMap model, @ModelAttribute("form") @Valid InitiateTransactionForm form,
            BindingResult result, WebRequest request, Principal principal, RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl;
        String logMessage;

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "customer/initiatetransaction";
        }

        User user = userService.getUserFromSession(principal);
        Transaction transaction = new Transaction();
        Account toAccount = accountService.getAccountByAccountId(form.getAccountId());
        transaction.setAmount(form.getAmount());
        transaction.setToAccount(toAccount);
        transaction.setComment(form.getComment());
        transaction.setTransferDate(form.getTransferDate());
        String serviceStatus = transactionService.initiateTransaction(transaction, user);

        if (serviceStatus.equalsIgnoreCase(SUCCESS)) {
            status = "success";
            message = "Your transaction is initiated and will be handled by our employees";
            redirectUrl = "redirect:/customer/myaccount";
        } else if (serviceStatus.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            status = "error";
            message = ERR_ACCOUNT_NOT_EXISTS;
            redirectUrl = "redirect:/customer/initiatetransaction";
        } else {
            status = "error";
            message = "An unhandled error occurred. Please contact the administrator";
            redirectUrl = "redirect:/customer/initiatetransaction";
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", "customer");

        logMessage = String.format("[Action=%s, Method=%s][Status=%s][Message=%s]", "initiatetransaction", "POST",
                serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }
}
