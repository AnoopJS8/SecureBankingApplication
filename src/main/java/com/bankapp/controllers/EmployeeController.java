package com.bankapp.controllers;

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

import com.bankapp.constants.Constants;
import com.bankapp.models.Account;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Transaction;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.ITransactionService;

@Controller
@Secured("ROLE_EMPLOYEE")
public class EmployeeController implements Constants {
    @Autowired
    private ISystemManagerService managerService;
    
    @Autowired ITransactionService transactionService;

    @Autowired
    private IProfileRequestService profileRequestService;
    
 // VIEW TRANSACTIONS
    @RequestMapping(value = "/employee/myaccount", method = RequestMethod.GET)
    public ModelAndView getMyAccount() {
        ModelAndView mv = new ModelAndView();
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

    // VIEW USER_PROFILE
    @RequestMapping(value = "/employee/requests", method = RequestMethod.GET)
    public ModelAndView getPendingProfileRequests() {

        List<ProfileRequest> requests = profileRequestService.getRequestsByStatus(S_PROFILE_UPDATE_PENDING);

        ModelAndView mv = new ModelAndView();
        mv.addObject("requests", requests);
        mv.setViewName("employee/viewRequests");
        return mv;
    }

    // AUTHORIZE USER_PROFILE
    @RequestMapping(value = "/authorize_userprofile", method = RequestMethod.POST, params = "action=Authorize")
    public ModelAndView authorizeProfileRequests(@ModelAttribute("row") ProfileRequest profileRequest,
            BindingResult result, WebRequest request, Errors errors) {
        ModelAndView mv = new ModelAndView();

        ProfileRequest requests = profileRequestService.getRequestById(profileRequest.getrId());

        String str = profileRequestService.authorizeRequest(requests);

        mv.addObject("result1", str);
        mv.setViewName("employee/viewRequests");

        return mv;
    }

    // DECLINE USER_PROFILE
    @RequestMapping(value = "/authorize_userprofile", method = RequestMethod.POST, params = "action=Decline")
    public ModelAndView declineProfileRequests(@ModelAttribute("row") ProfileRequest rId, BindingResult result,
            WebRequest request, Errors errors) {
        ModelAndView mv = new ModelAndView();

        ProfileRequest requests = profileRequestService.getRequestById(rId.getrId());

        String str = profileRequestService.declineRequest(requests);

        mv.addObject("result1", str);
        mv.setViewName("employee/viewRequests");

        return mv;
    }

    // EMPLOYEE AUTHORIZE TRANSACTION
    @RequestMapping(value = "/employee/transactions", method = RequestMethod.POST, params = "action=Authorize")
    public ModelAndView approvetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors) {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = managerService.getTransactionbyid(Id.getTransactionId());

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

        Transaction transaction = managerService.getTransactionbyid(Id.getTransactionId());

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

        Transaction transaction = managerService.getTransactionbyid(Id.getTransactionId());

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
