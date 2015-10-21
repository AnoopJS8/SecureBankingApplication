/**

 * 
 */
package com.bankapp.controllers;

import java.security.Principal;
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

import com.bankapp.exceptions.EmailDoesNotExist;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserAlreadyExistException;
import com.bankapp.exceptions.UserIdDoesNotExist;
import com.bankapp.exceptions.UserNameExistsException;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
//import com.bankapp.models.Requests;

import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.IUserService;
import com.bankapp.services.IRegularEmployee;
import com.bankapp.constants.Constants;


@Controller
public class RegularEmployeeController implements Constants{
	 @Autowired
	    private ISystemManagerService manager;
	 	private IRegularEmployee trans;
	 
	    private final Logger LOGGER = Logger.getLogger(RegularEmployeeController.class);

	    // VIEW TRANSACTIONS
	    @RequestMapping(value = "/viewtransactions", method = RequestMethod.GET)
	    public ModelAndView getPendingTransaction() {
	        List<Transaction> transactions = manager.getTransactionsByStatus(S_PENDING);
	        ModelAndView mv = new ModelAndView();
	        mv.addObject("transactions", transactions);
	        mv.setViewName("employee/employee_transactions");
	        return mv;
	    }
	    
	    
	    // EMPLOYEE AUTHORIZE TRANSACTION 
	    @RequestMapping(value = "/employee_transactions", method = RequestMethod.POST, params="action=Authorize")
	    public ModelAndView approvetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
	            WebRequest request, Errors errors, Principal principal) {
	        ModelAndView mv = new ModelAndView();
	        System.out.println("Entered Approve");
	        System.out.println("Transaction" + Id.getTransactionId());

	        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());

	        Account FromAccount = transaction.getFromAccount();
	        Account ToAccount = transaction.getToAccount();
	        Double AmountToBeSent = transaction.getAmount();
	        System.out.println(AmountToBeSent);

	        Double FromAccountBalance = FromAccount.getBalance();
	        System.out.println(FromAccountBalance);

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
	        System.out.println("Done");
	        mv.setViewName("employee/employee_transactions");

	        return mv;
	    }

	    // EMPLOYEE DECLINE TRANSACTION 
	    @RequestMapping(value = "/employee_transactions", method = RequestMethod.POST, params="action=Decline")
	    public ModelAndView declinetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
	            WebRequest request, Errors errors, Principal principal) {
	        ModelAndView mv = new ModelAndView();
	        System.out.println("Entered Decline");
	        System.out.println("Transaction" + Id.getTransactionId());

	        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());

//	        Account FromAccount = transaction.getFromAccount();
//	        Account ToAccount = transaction.getToAccount();
//	        Double AmountToBeSent = transaction.getAmount();
//	        System.out.println(AmountToBeSent);
//
//	        Double FromAccountBalance = FromAccount.getBalance();
//	        System.out.println(FromAccountBalance);
//
	        String str = "";
//
//	        if (FromAccountBalance > AmountToBeSent) {
//	            manager.reflectChangesToSender(FromAccount, FromAccountBalance, AmountToBeSent);
//	            Double ToAccountBalance = ToAccount.getBalance();
//	            manager.reflectChangesToReceiver(ToAccount, ToAccountBalance, AmountToBeSent);
//	            str = manager.approveTransaction(transaction);
//	        } else {
	            str = manager.declineTransaction(transaction);
//	        }

	        mv.addObject("result1", str);
	        System.out.println("Declined");
	        mv.setViewName("employee/employee_transactions");

	        return mv;
	    }

	    // EMPLOYEE MODIFY TRANSACTION 
	    @RequestMapping(value = "/employee_transactions", method = RequestMethod.POST, params="action=Modify")
	    public ModelAndView modifytransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
	            WebRequest request, Errors errors, Principal principal) {
	        ModelAndView mv = new ModelAndView();
	        System.out.println("*********************************************************************************1");
	        System.out.println("Transaction" + Id.getTransactionId());

	        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());
	        
	    //    Account FromAccount = transaction.getFromAccount();
	        Account ToAccount = transaction.getToAccount();
	        Long to_acct=ToAccount.getAccId();
	        String a=to_acct.toString();
	        Double AmountToBeSent = transaction.getAmount();
	        System.out.println("***************************************************************************************2"+a);
	       
	        System.out.println(AmountToBeSent);

	    //    Double FromAccountBalance = FromAccount.getBalance();
	     //  System.out.println(ToAccount);

	        mv.addObject("modify_transaction", transaction);
	        System.out.println("Done");
	        mv.setViewName("employee/modify_transactions");

	        return mv;
	    }
	    // EMPLOYEE MODIFY AMOUNT
	    @RequestMapping(value = "/modifyamount_transactions", method = RequestMethod.POST, params="action=Updateamount")
	    public ModelAndView updatemodifytransaction(@ModelAttribute("row") Transaction Id,@ModelAttribute("amount") Double Amount, BindingResult result,
	            WebRequest request, Errors errors, Principal principal) {
	        ModelAndView mv = new ModelAndView();
	        System.out.println("***********************************************************************************2");
	        System.out.println("Transaction" + Id.getTransactionId());

	        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());
	        
	        Account FromAccount = transaction.getFromAccount();
	        Account ToAccount = transaction.getToAccount();

	        System.out.println(Amount);
 
	      

	        Double FromAccountBalance = FromAccount.getBalance();
	        System.out.println(FromAccountBalance);

	        String str = "";
	        
	        if (FromAccountBalance > Amount) {
	            manager.reflectChangesToSender(FromAccount, FromAccountBalance, Amount);
	            Double ToAccountBalance = ToAccount.getBalance();
	            manager.reflectChangesToReceiver(ToAccount, ToAccountBalance, Amount);
	            str = manager.approveTransaction(transaction);
	        } else {
	            str = manager.declineTransaction(transaction);
	        }

	        mv.addObject("result1", str);
	        System.out.println("Done");
	        mv.setViewName("employee/modify_transactions");

	        return mv;
	    }

	    // EMPLOYEE MODIFY ACCOUNT
	    @RequestMapping(value = "/modifytoAccount_transactions", method = RequestMethod.POST, params="action=Updateaccount")
	    public ModelAndView updateaccounttransaction(@ModelAttribute("row") Transaction Id,@ModelAttribute("to_Account") Account to_Account, BindingResult result,
	            WebRequest request, Errors errors, Principal principal) {
	        ModelAndView mv = new ModelAndView();
	        System.out.println("***********************************************************************************2");
	        System.out.println("Transaction" + Id.getTransactionId());

	        Transaction transaction = manager.getTransactionbyid(Id.getTransactionId());
	        
	        Account FromAccount = transaction.getFromAccount();
	       // Account ToAccount = transaction.getToAccount();
	        Double AmountToBeSent = transaction.getAmount();
	        
	        System.out.println("**********************************************************************************3"+to_Account);
 
	      

	        Double FromAccountBalance = FromAccount.getBalance();
	        System.out.println(FromAccountBalance);

	        String str = "";
	        
	        if (FromAccountBalance > AmountToBeSent) {
	            manager.reflectChangesToSender(FromAccount, FromAccountBalance, AmountToBeSent);
	            Double ToAccountBalance = to_Account.getBalance();
	            manager.reflectChangesToReceiver(to_Account, ToAccountBalance, AmountToBeSent);
	            str = manager.approveTransaction(transaction);
	        } else {
	            str = manager.declineTransaction(transaction);
	        }

	        mv.addObject("result1", str);
	        System.out.println("Done");
	        mv.setViewName("employee/modify_transactions");

	        return mv;
	    }
	    
//	    @RequestMapping(value = "/employee_viewuser_byemail", method = RequestMethod.GET)
//	    public ModelAndView viewUserProfile(String email) {
//
//	        User user = null;
//
//	        try {
//	            user = manager.viewUserByEmail(email);
//	        } catch (EmailDoesNotExist e) {
//	            String message = String.format("Action: %s, Message: %s", "EmailDoesNotExist", e.getMessage());
//	            LOGGER.error(message);
//	            return null;
//	        }
//
//	        ModelAndView mv = new ModelAndView();
//	        mv.addObject("viewuser", user);
//	        mv.setViewName("employee/employee_view");
//	        return mv;
//	    }
//    
	    
	    
//	    // EMPLOYEE VIEW USER BY EMAIL
//	    @RequestMapping(value = "/employee_viewuser_byemail", method = RequestMethod.GET)
//	    public ModelAndView getuser_byemail(String email) {
//
//	        User user = null;
//
//	        try {
//	            user = manager.viewUserByEmail(email);
//	        } catch (EmailDoesNotExist e) {
//	            String message = String.format("Action: %s, Message: %s", "EmailDoesNotExist", e.getMessage());
//	            LOGGER.error(message);
//	            return null;
//	        }
//
//	        ModelAndView mv = new ModelAndView();
//	        mv.addObject("viewuser", user);
//	        mv.setViewName("employee/employee_view");
//	        return mv;
//	    }
//
//	    // EMPLOYEE VIEW USER BY ID 
//	    @RequestMapping(value = "/employee_viewuser_byid", method = RequestMethod.GET)
//	    public ModelAndView getuser_byid(Long id) {
//
//	        User user = null;
//
//	        try {
//	            user = manager.viewUserById(id);
//	        } catch (UserIdDoesNotExist e) {
//	            String message = String.format("Action: %s, Message: %s", "EmailDoesNotExist", e.getMessage());
//	            LOGGER.error(message);
//	            return null;
//	        }
//
//	        ModelAndView mv = new ModelAndView();
//	        mv.addObject("viewuser", user);
//	        mv.setViewName("employee/employee_view");
//	        return mv;
//	    }
	
//	@Autowired
//	private IRegularEmployee pending;
//	//private IUserService userservice;
//	
//	
////	@RequestMapping(value = "/employeependingtransaction", method = RequestMethod.GET)
////	public ModelAndView getPendingTransaction() {
////		List<Transaction> transactions = pending.getTransactionByTflag("Pending");
////		ModelAndView mv = new ModelAndView();
////	
////		mv.addObject("pending", transactions);
////		mv.setViewName("/pendingtransaction");
////		return mv;
////	}
//	
//	@RequestMapping(value = "/employeerequest", method = RequestMethod.GET)
//	public ModelAndView getRequest() {
//		List<Requests> user1 = pending.getRequest((long)101);
//		//List<Requests> request = userservice.getRequest("Pending");
//		System.out.println("users" + user1);
//		ModelAndView mv = new ModelAndView();
//	
//		mv.addObject("greet", user1);
//		mv.setViewName("EmployeeRetrieveUserID");
//		return mv;
//	}
//	
	
//	@RequestMapping(value = "/EmployeeRetrieveUserID", method = RequestMethod.POST)
//	public ModelAndView searchUserID() {
//		
//		//List<Transaction> transactions = pending.getTransactionByTflag("Pending");
//		ModelAndView mv = new ModelAndView();
//		
//		mv.addObject("pending", transactions);
//		mv.setViewName("employee/pendingtransaction");
//		return mv;
//	}
	
//	 @RequestMapping(value="/EmployeeRetrieveUserID", method=RequestMethod.GET)
//	    public String greetingForm(Model model) {
//	        model.addAttribute("greeting", new Greeting());
//	        return "greeting";
//	    }
//
//	    @RequestMapping(value="/greeting", method=RequestMethod.POST)
//	    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
//	        model.addAttribute("greeting", greeting);
//	        return "result";
//	    }
	     
	
	
	
	
}
