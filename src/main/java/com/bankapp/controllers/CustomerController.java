package com.bankapp.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.models.Transaction;
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
    
    @RequestMapping(value = "/customer/authorizemerchant", method = RequestMethod.GET)
    public ModelAndView authorizemerchant() {
        ModelAndView mv = new ModelAndView("/customer/authorizemerchant");
        mv.addObject("role", "customer");
        List<Transaction> transactions = transactionService.getMerchantRequests(S_PENDING_CUSTOMER_VERIFICATION);
        mv.addObject("transactions", transactions);
        return mv;
    }
    
    @RequestMapping(value = "/customer/approverequest", method = RequestMethod.POST)
    public String approveRequest(@ModelAttribute("transaction") Transaction transaction, BindingResult result,
            RedirectAttributes attributes) {
        Message message;        
        String msg = transactionService.actionOnRequest(transaction.getTransactionId(), S_CUSTOMER_VERIFIED);
        if (msg.equals(SUCCESS)) {
            message = new Message("success", "Request has been approved ");
        } else {
            message = new Message("error", "error please try again");
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/customer/authorizemerchant";
    }
    
    @RequestMapping(value = "/customer/declinerequest", method = RequestMethod.POST)
    public String declineRequest(@ModelAttribute("transaction") Transaction transaction, BindingResult result,
            RedirectAttributes attributes) {
        Message message;        
        String msg = transactionService.actionOnRequest(transaction.getTransactionId(), S_CUSTOMER_DECLINED);
        if (msg.equals(SUCCESS)) {
            message = new Message("success", "Request has been declined ");
        } else {
            message = new Message("error", "error please try again");
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/customer/authorizemerchant";
    }
}
