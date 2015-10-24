package com.bankapp.controllers;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@Secured({ "ROLE_CUSTOMER", "ROLE_MERCHANT" })
public class CommonController implements Constants {

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

    @RequestMapping(value = { "/customer/myaccount", "/merchant/myaccount" }, method = RequestMethod.GET)
    public ModelAndView getTransactions(Principal principal, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        User loggedInUser = userService.getUserFromSession(principal);
        Account account = accountService.getAccountByUser(loggedInUser);
        List<Transaction> transactions = transactionService.getTransactionsByAccount(account, account);
        mv.addObject("accounts", account);
        mv.addObject("transactions", transactions);
        mv.addObject("role", role);
        mv.setViewName("common/myaccount");

        String message = String.format("[Action=%s, Method=%s, Role=%s][User=%s, Account=%s, NumberOfTransactions=%s]",
                "myaccount", "GET", role, loggedInUser.getEmail(), account.getAccId(), transactions.size());
        LOGGER.info(message);

        return mv;
    }

    @RequestMapping(value = { "/customer/transferfunds", "/merchant/transferfunds" }, method = RequestMethod.GET)
    public ModelAndView transferFunds(HttpServletRequest request) {

        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        ModelAndView mv = new ModelAndView("common/transferfunds");
        mv.addObject("form", new TransferFundsForm());
        mv.addObject("role", role);

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s]", "transferfunds", "GET", role);
        LOGGER.info(logMessage);

        return mv;
    }

    @RequestMapping(value = { "/customer/transferfunds", "/merchant/transferfunds" }, method = RequestMethod.POST)
    public String saveTransaction(@AuthenticationPrincipal UserDetails activeUser, final ModelMap model,
            @ModelAttribute("form") @Valid TransferFundsForm form, BindingResult result, HttpServletRequest request,
            RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl;
        String logMessage;
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "common/transferfunds";
        }

        String fromEmail = activeUser.getUsername();
        String toEmail = form.getEmail();
        Transaction transaction = new Transaction();
        transaction.setAmount(form.getAmount());
        transaction.setComment(form.getComment());

        String serviceStatus = transactionService.saveTransaction(fromEmail, toEmail, transaction);

        if (serviceStatus.equalsIgnoreCase(LESS_BALANCE)) {
            status = "error";
            message = "You are low on balance, the transaction cannot go through.";
            redirectUrl = "redirect:/" + role + "/transferfunds";
        } else if (serviceStatus.equalsIgnoreCase(SUCCESS)) {
            status = "success";
            message = "Money transfered successfully";
            redirectUrl = "redirect:/" + role + "/myaccount";
        } else if (serviceStatus.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            status = "error";
            message = ERR_ACCOUNT_NOT_EXISTS;
            redirectUrl = "redirect:/" + role + "/transferfunds";
        } else if (serviceStatus.equalsIgnoreCase(CRITICAL)) {
            status = "success";
            message = "Its a critical transaction, so it will be handled by our employees shortly";
            redirectUrl = "redirect:/" + role + "/myaccount";
        } else {
            status = "error";
            message = "An unhandled error occurred. Please contact the administrator";
            redirectUrl = "redirect:/" + role + "/transferfunds";
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", role);

        logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "transferfunds", "POST",
                role, serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }

    @RequestMapping(value = { "/customer/initiatetransaction",
            "/merchant/initiatetransaction" }, method = RequestMethod.GET)
    public ModelAndView initiateTransaction(HttpServletRequest request) {
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        ModelAndView mv = new ModelAndView("common/initiatetransaction");
        mv.addObject("form", new InitiateTransactionForm());
        mv.addObject("role", role);

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s]", "initiatetransaction", "GET", role);
        LOGGER.info(logMessage);

        return mv;
    }

    @RequestMapping(value = { "/customer/initiatetransaction",
            "/merchant/initiatetransaction" }, method = RequestMethod.POST)
    public String initiateTransaction(@AuthenticationPrincipal UserDetails activeUser, final ModelMap model,
            @ModelAttribute("form") @Valid InitiateTransactionForm form, BindingResult result,
            HttpServletRequest request, RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl;
        String logMessage;
        String role;

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            role = "customer";
        } else {
            role = "merchant";
        }

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "common/initiatetransaction";
        }

        String fromEmail = activeUser.getUsername();
        String toEmail = form.getEmail();
        Transaction transaction = new Transaction();
        transaction.setAmount(form.getAmount());
        transaction.setComment(form.getComment());
        transaction.setTransferDate(form.getTransferDate());
        String serviceStatus = transactionService.initiateTransaction(fromEmail, toEmail, transaction);

        if (serviceStatus.equalsIgnoreCase(SUCCESS)) {
            status = "success";
            message = "Your transaction is initiated and will be handled by our employees";
            redirectUrl = "redirect:/" + role + "/myaccount";
        } else if (serviceStatus.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            status = "error";
            message = ERR_ACCOUNT_NOT_EXISTS;
            redirectUrl = "redirect:/" + role + "/initiatetransaction";
        } else {
            status = "error";
            message = "An unhandled error occurred. Please contact the administrator";
            redirectUrl = "redirect:/" + role + "/initiatetransaction";
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", role);

        logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "initiatetransaction",
                "POST", role, serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }
}