package com.bankapp.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
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
import com.bankapp.models.Account;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Transaction;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
@Secured("ROLE_EMPLOYEE")

public class EmployeeController implements Constants {
    @Autowired
    private ISystemManagerService managerService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IProfileRequestService profileRequestService;

    @Autowired
    private IUserService userService;

    // VIEW TRANSACTIONS
    @RequestMapping(value = "/employee/myaccount", method = RequestMethod.GET)
    public ModelAndView getMyAccount(Principal principal) {
        ModelAndView mv = new ModelAndView();
        if (userService.hasMissingFields(principal)) {
            mv.addObject("message",
                    new Message("error", "You are missing important details. Please update your profile urgently"));
        }
        mv.setViewName("employee/myaccount");
        return mv;
    }

    // VIEW TRANSACTIONS
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.GET)
    public ModelAndView getPendingTransaction() {
        List<Transaction> transactions = transactionService.getPendingTransactions();
        ModelAndView mv = new ModelAndView();
        mv.addObject("transactions", transactions);
        mv.setViewName("employee/viewTransactions");
        return mv;
    }

    @RequestMapping(value = "/employee/requests", method = RequestMethod.GET)
    public ModelAndView getPendingProfileRequests() {

        List<ProfileRequest> requests = profileRequestService.getRequestsByStatus(S_PROFILE_UPDATE_PENDING);
        ModelAndView mv = new ModelAndView();
        mv.addObject("requests", requests);
        mv.setViewName("employee/viewRequests");
        return mv;
    }

    @RequestMapping(value = "/employee/requests", method = RequestMethod.POST, params = "action=Authorize")
    public String authorizeProfileRequests(@ModelAttribute("row") ProfileRequest profileRequest, BindingResult result,
            RedirectAttributes attributes) {
        ProfileRequest pRequest = profileRequestService.getRequestById(profileRequest.getrId());
        String serviceStatus = profileRequestService.authorizeRequest(pRequest);
        if (serviceStatus.equalsIgnoreCase(S_PROFILE_UPDATE_VERIFIED)) {
            attributes.addFlashAttribute("message", new Message("success", "Request has been approved successfully"));
        } else {
            attributes.addFlashAttribute("message", new Message("error", serviceStatus));
        }

        return "redirect:/employee/requests";
    }

    // DECLINE USER_PROFILE
    @RequestMapping(value = "/employee/requests", method = RequestMethod.POST, params = "action=Decline")
    public String declineProfileRequests(@ModelAttribute("row") ProfileRequest profileRequest, BindingResult result,
            WebRequest request, Errors errors, RedirectAttributes attributes) {
        ProfileRequest pRequest = profileRequestService.getRequestById(profileRequest.getrId());
        String serviceStatus = profileRequestService.declineRequest(pRequest);
        if (serviceStatus.equalsIgnoreCase(S_PROFILE_UPDATE_DECLINED)) {
            attributes.addFlashAttribute("message", new Message("success", "Request has been declined successfully"));
        } else {
            attributes.addFlashAttribute("message", new Message("error", serviceStatus));
        }

        return "redirect:/employee/requests";
    }

    // EMPLOYEE AUTHORIZE TRANSACTION
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.POST, params = "action=Authorize")
    public ModelAndView approvetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors) {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = managerService.getTransactionById(Id.getTransactionId());

        Account FromAccount = transaction.getFromAccount();
        Account ToAccount = transaction.getToAccount();
        Double AmountToBeSent = transaction.getAmount();

        Double FromAccountBalance = FromAccount.getBalance();

        String str = "";

        if (FromAccountBalance > AmountToBeSent) {
            managerService.reflectChangesToSender(FromAccount, FromAccountBalance, AmountToBeSent);
            Double ToAccountBalance = ToAccount.getBalance();
            managerService.reflectChangesToReceiver(ToAccount, ToAccountBalance, AmountToBeSent);
            str = managerService.approveTransaction(transaction);
        } else {
            str = managerService.declineTransaction(transaction);
        }

        mv.addObject("result1", str);
        mv.setViewName("employee/viewTransactions");

        return mv;
    }

    // EMPLOYEE DECLINE TRANSACTION
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.POST, params = "action=Decline")
    public ModelAndView declinetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors) {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = managerService.getTransactionById(Id.getTransactionId());

        String str = managerService.declineTransaction(transaction);

        mv.addObject("result1", str);
        mv.setViewName("employee/viewTransactions");

        return mv;
    }

    // EMPLOYEE MODIFY TRANSACTION
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.POST, params = "action=Modify")
    public ModelAndView modifytransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors) {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = managerService.getTransactionById(Id.getTransactionId());

        /*
         * Account ToAccount = transaction.getToAccount(); String to_acct =
         * ToAccount.getAccId(); String a = to_acct.toString(); Double
         * AmountToBeSent = transaction.getAmount(); Date transferDate =
         * transaction.getTransferDate();
         */
        mv.addObject("transaction", transaction);
        mv.setViewName("employee/editTransactions");

        return mv;
    }

}
