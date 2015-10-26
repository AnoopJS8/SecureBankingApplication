
package com.bankapp.controllers;

import java.security.Principal;
import java.util.Date;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
import com.bankapp.forms.UserPaymentForm;
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
    @RequestMapping(value = "/merchant/userpayment", method = RequestMethod.GET)
    public ModelAndView askUserPayment() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("form", new UserPaymentForm());
        mv.addObject("role", "merchant");
        mv.setViewName("merchant/userpayment");
        return mv;
    }

    @RequestMapping(value = "/merchant/userpayment", method = RequestMethod.POST)
    public String askUserPayment(final ModelMap model, @ModelAttribute("form") @Valid UserPaymentForm form,
            BindingResult result, WebRequest request, Errors errors, Principal principal,
            RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl = "redirect:/merchant/myaccount";
        String logMessage;

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "merchant/userpayment";
        }

        User user = userService.getUserFromSession(principal);
        Transaction transaction = new Transaction();
        Account fromAccount = accountService.getAccountByUser(userService.getUserByEmail(form.getEmail()));
        transaction.setAmount(form.getAmount());
        transaction.setFromAccount(fromAccount);
        transaction.setComment(form.getComment());
        transaction.setTransferDate(new Date());
        String serviceStatus = transactionService.askCustomerPayment(transaction, user);

        if (serviceStatus.equalsIgnoreCase(SUCCESS)) {
            status = "success";
            message = "Notified the user for approval and transaction is in pending state till customers approval";
        } else if (serviceStatus.equalsIgnoreCase(ERR_ACCOUNT_NOT_EXISTS)) {
            status = "error";
            message = ERR_ACCOUNT_NOT_EXISTS;
            redirectUrl = "redirect:/merchant/userpayment";
        } else {
            status = "error";
            message = "An unhandled error occurred. Please contact the administrator";
            redirectUrl = "redirect:/merchant/userpayment";
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", "merchant");

        logMessage = String.format("[Action=%s, Method=%s][Status=%s][Message=%s]", "userpayment", "POST",
                serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;
    }

}
