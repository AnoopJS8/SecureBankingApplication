/**

 * 
 */
package com.bankapp.controllers;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.bankapp.forms.TransferFundsForm;
import com.bankapp.models.Account;
import com.bankapp.models.PersonalIdentificationInfo;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Transaction;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.ITransactionService;

@Controller
public class RegularEmployeeController implements Constants {
    @Autowired
    private ISystemManagerService manager;

    @Autowired
    private IProfileRequestService req;

    @Autowired
    private ITransactionService transactionService;

    private final Logger LOGGER = Logger.getLogger(RegularEmployeeController.class);
    
    @RequestMapping(value = "/employee/myaccount", method = RequestMethod.GET)
    public ModelAndView AdminDetails() {
        ModelAndView mv = new ModelAndView("/employee/myaccount");
        return mv;
    }

    // VIEW TRANSACTIONS
    @RequestMapping(value = "/employee/viewtransactions", method = RequestMethod.GET)
    public ModelAndView getPendingTransaction() {
        List<Transaction> transactions = manager.getTransactionsByStatus(S_PENDING);
        ModelAndView mv = new ModelAndView();
        mv.addObject("transactions", transactions);
        mv.setViewName("employee/employee_transactions");
        return mv;
    }

    // VIEW USER_PROFILE
    @RequestMapping(value = "/employee/viewuserprofile", method = RequestMethod.GET)
    public ModelAndView getPendingProfileRequests() {
        List<ProfileRequest> requests = req.getRequestsByStatus(S_PROFILE_UPDATE_PENDING);
        ModelAndView mv = new ModelAndView();
        mv.addObject("profilerequest", requests);
        mv.setViewName("employee/employee_view");
        return mv;
    }

    // AUTHORIZE USER_PROFILE
    @RequestMapping(value = "/authorize_userprofile", method = RequestMethod.POST, params = "action=Authorize")
    public ModelAndView authorizeProfileRequests(@ModelAttribute("row") ProfileRequest rId, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        ProfileRequest requests = req.getRequestById(rId.getrId());
        String str = "";
        str = req.authorizeRequest(requests);
        mv.addObject("result1", str);
        mv.setViewName("employee/employee_view");
        return mv;
    }

    // DECLINE USER_PROFILE
    @RequestMapping(value = "/authorize_userprofile", method = RequestMethod.POST, params = "action=Decline")
    public ModelAndView declineProfileRequests(@ModelAttribute("row") ProfileRequest rId, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        ProfileRequest requests = req.getRequestById(rId.getrId());
        String str = "";
        str = req.declineRequest(requests);
        mv.addObject("result1", str);
        mv.setViewName("employee/employee_view");

        return mv;
    }

    // EMPLOYEE AUTHORIZE TRANSACTION
    @RequestMapping(value = "/employee_transactions", method = RequestMethod.POST, params = "action=Authorize")
    public ModelAndView approvetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());

        Account FromAccount = transaction.getFromAccount();
        Account ToAccount = transaction.getToAccount();
        Double AmountToBeSent = transaction.getAmount();

        Double FromAccountBalance = FromAccount.getBalance();

        String str = "";

        if (FromAccountBalance > AmountToBeSent) {
            manager.reflectChangesToSender(FromAccount, FromAccountBalance, AmountToBeSent);
            Double ToAccountBalance = ToAccount.getBalance();
            manager.reflectChangesToReceiver(ToAccount, ToAccountBalance, AmountToBeSent);
            str = manager.approveTransaction(transaction);
        } else {
            str = manager.declineTransaction(transaction);
        }

        mv.addObject("result1", str);
        mv.setViewName("employee/employee_transactions");

        return mv;
    }

    // EMPLOYEE DECLINE TRANSACTION
    @RequestMapping(value = "/employee_transactions", method = RequestMethod.POST, params = "action=Decline")
    public ModelAndView declinetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());

        String str = "";
        str = manager.declineTransaction(transaction);

        mv.addObject("result1", str);
        mv.setViewName("employee/employee_transactions");

        return mv;
    }

    // EMPLOYEE MODIFY TRANSACTION
    @RequestMapping(value = "/employee_transactions", method = RequestMethod.POST, params = "action=Modify")
    public ModelAndView modifytransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());

        Account ToAccount = transaction.getToAccount();
        String to_acct = ToAccount.getAccId();
        String a = to_acct.toString();
        Double AmountToBeSent = transaction.getAmount();
        Date transferDate = transaction.getTransferDate();

        mv.addObject("modify_transaction", transaction);
        mv.setViewName("employee/modify_transactions");

        return mv;
    }

    // // EMPLOYEE MODIFY DATE
    @RequestMapping(value = "/modifyDate_transactions", method = RequestMethod.POST, params = "action=Updatedate")
    public ModelAndView updateDateTransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            @ModelAttribute("transferDate") String transferDate, WebRequest request, Errors errors, Principal principal)
                    throws ParseException {
        ModelAndView mv = new ModelAndView();

        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

        Date TransferDate = transaction.getTransferDate();
        // throw new InvalidDateFormat(transferDate);
        Date date = new Date();
        date = sdf.parse(sdf.format(date));
        Date new_date = sdf.parse(transferDate);
        String str = "";
        if (new_date.compareTo(date) == 0 || new_date.compareTo(date) > 0) {
            str = manager.modifyTransaction(transaction, new_date);
        } else {
            str = manager.declineTransaction(transaction);
        }

        mv.addObject("result1", str);
        mv.setViewName("employee/modify_transactions");

        return mv;

    }


}
