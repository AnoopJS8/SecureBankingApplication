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
import com.bankapp.services.ISystemManagerService;
import com.bankapp.constants.Constants;;;

/**
 * @author Nitesh Dhanpal
 *
 */
@Controller
public class SystemManagerController implements Constants {

	@Autowired
	private ISystemManagerService manager;

	private final Logger LOGGER = Logger.getLogger(SystemManagerController.class);

	@RequestMapping(value = "/criticaltransaction", method = RequestMethod.GET)
	public ModelAndView getCriticalTransaction() {
		List<Transaction> transactions = manager.getTransactionsByStatus(S_OTP_VERIFIED);
		ModelAndView mv = new ModelAndView();
		mv.addObject("critical", transactions);
		mv.setViewName("/manager/viewTransaction");
		return mv;
	}

	@RequestMapping(value = "/pendingtransaction", method = RequestMethod.GET)
	public ModelAndView getInitiatedTransaction() {
		List<Transaction> transactions = manager.getTransactionsByStatus(S_PENDING);
		ModelAndView mv = new ModelAndView();
		mv.addObject("pending", transactions);
		mv.setViewName("/manager/viewPending");
		return mv;
	}

	@RequestMapping(value = "/getUserById", method = RequestMethod.GET)
	public ModelAndView getuserId() {
		ModelAndView mv = new ModelAndView();

		mv.setViewName("/manager/viewUserByIdForm");
		return mv;
	}

	@RequestMapping(value = "/getUserByEmail", method = RequestMethod.GET)
	public ModelAndView getuserEmail() {
		ModelAndView mv = new ModelAndView();

		mv.setViewName("/manager/viewUserByEmailForm");
		return mv;
	}

	@RequestMapping(value = "/approvetransaction", method = RequestMethod.POST)
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
			str = "Unsuccessfull";
		}
		System.out.println(str);
		mv.addObject("result1", str);
		System.out.println("Done");
		mv.setViewName("/manager/viewTransaction");

		return mv;
	}

	@RequestMapping(value = "/manager_adduser", method = RequestMethod.POST)
	public ModelAndView addUser(User user_request) {
		User user = null;

		try {
			user = manager.addUser(user_request);
		} catch (EmailExistsException e) {
			String message = String.format("Action: %s, Message: %s", "email_exists", e.getMessage());
			LOGGER.error(message);
			return null;
		} catch (UserNameExistsException e) {
			String message = String.format("Action: %s, Message: %s", "username_exists", e.getMessage());
			LOGGER.error(message);
			return null;
		} catch (UserAlreadyExistException e) {
			String message = String.format("Action: %s, Message: %s", "user_exists", e.getMessage());
			LOGGER.error(message);
			return null;
		}

		ModelAndView mv = new ModelAndView();
		mv.addObject("manager_ceateuser", user);
		mv.setViewName("/manager/manager_view");

		return mv;

	}

	@RequestMapping(value = "/manager_viewuser_byemail", method = RequestMethod.POST)
	public ModelAndView getuser_byemail(@ModelAttribute("email") String email, BindingResult result, WebRequest request,
	        Errors errors, Principal principal) {

		User user = null;

		try {
			user = manager.viewUserByEmail(email);
		} catch (EmailDoesNotExist e) {
			String message = String.format("Action: %s, Message: %s", "EmailDoesNotExist", e.getMessage());
			LOGGER.error(message);
			return null;
		}

		ModelAndView mv = new ModelAndView();
		mv.addObject("viewuser", user);
		mv.setViewName("/manager/viewUser");
		return mv;
	}

	@RequestMapping(value = "/manager_viewuser_byid", method = RequestMethod.POST)
	public ModelAndView getuser_byid(@ModelAttribute("userid") String Id, BindingResult result, WebRequest request,
	        Errors errors, Principal principal) {

		User user = null;

		try {
			user = manager.viewUserById(Id);
		} catch (UserIdDoesNotExist e) {
			String message = String.format("Action: %s, Message: %s", "EmailDoesNotExist", e.getMessage());
			LOGGER.error(message);
			return null;
		}

		ModelAndView mv = new ModelAndView();
		mv.addObject("viewuser", user);
		mv.setViewName("/manager/viewUser");
		return mv;
	}

}