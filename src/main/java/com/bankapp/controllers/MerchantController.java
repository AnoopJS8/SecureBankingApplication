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
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
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
import com.bankapp.forms.transferFundsForm;
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

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        CustomDateEditor editor = new CustomDateEditor(new SimpleDateFormat("MM/dd/yyyy"), true);
        binder.registerCustomEditor(Date.class, editor);
    }

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
        ModelAndView mv = new ModelAndView("merchant/transferfunds", "form", new transferFundsForm());
        return mv;
    }

    @RequestMapping(value = "/merchant/transferfunds", method = RequestMethod.POST)
    public String saveTransaction(final ModelMap model, @ModelAttribute("form") @Valid transferFundsForm form,
            BindingResult result, WebRequest request, Errors errors, Principal principal, RedirectAttributes attributes) {
                
        String status;
        String message;
        String redirectUrl;
        String logMessage;
        
        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "merchant/transferfunds";
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
            redirectUrl = "redirect:/merchant/transferfunds";
        } else if (dbStatus.equalsIgnoreCase(SUCCESS)) {
            status = "success";
            message = "Money transfered successfully";
            redirectUrl = "redirect:/merchant/myaccount";
        } else if (dbStatus.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            status = "error";
            message = ERR_ACCOUNT_NOT_EXISTS;
            redirectUrl = "redirect:/merchant/transferfunds";
        } else if (dbStatus.equalsIgnoreCase(CRITICAL)) {
            status = "success";
            message = "Its a critical transaction, so it will be handled by our employees shortly";
            redirectUrl = "redirect:/merchant/myaccount";
        } else {
            status = "error";
            message = "An unhandled error occurred. Please contact the administrator";
            redirectUrl = "redirect:/merchant/transferfunds";
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", "merchant");

        logMessage = String.format("[Action=%s, Method=%s][Status=%s][Message=%s]", "transferfunds", "POST", dbStatus,
                message);
        LOGGER.info(logMessage);

        return redirectUrl;
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
    public ModelAndView askUserPayment(@ModelAttribute("transaction") @Valid Transaction transaction,
            BindingResult result, WebRequest request, Errors errors, Principal principal) {
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
        } else if (message.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            mv.addObject("message", ERR_ACCOUNT_NOT_EXISTS);
            mv.setViewName("error");
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
    public String initiateTransaction(final ModelMap model, @ModelAttribute("form") @Valid InitiateTransactionForm form,
            BindingResult result, WebRequest request, Errors errors, Principal principal, RedirectAttributes attributes) {
        ModelAndView mv = new ModelAndView();
        
        String status;
        String message;
        String redirectUrl;
        String logMessage;
        
        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "merchant/initiatetransaction";
        }

        User user = userService.getUserFromSession(principal);
        Transaction transaction = new Transaction();
        Account toAccount = accountService.getAccountByAccountId(form.getAccountId());
        transaction.setAmount(form.getAmount());
        transaction.setToAccount(toAccount);
        transaction.setComment(form.getComment());
        transaction.setTransferDate(form.getTransferDate());
        String dbStatus = transactionService.initiateTransaction(transaction, user);

        if (dbStatus.equalsIgnoreCase(SUCCESS)) {
            status = "success";
            message = "Your transaction is initiated and will be handled by our employees";
            redirectUrl = "redirect:/merchant/myaccount";
        } else {
            status = "error";
            message = "An unhandled error occurred. Please contact the administrator";
            redirectUrl = "redirect:/merchant/initiatetransaction";
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", "merchant");

        logMessage = String.format("[Action=%s, Method=%s][Status=%s][Message=%s]", "initiatetransaction", "POST",
                dbStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
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
