/**
 * 
 */
package com.bankapp.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserNameExistsException;
import com.bankapp.forms.AddEmployeeForm;
import com.bankapp.forms.UpdateUsersForm;
import com.bankapp.forms.ViewByEmailForm;
import com.bankapp.listeners.OnRegistrationCompleteEvent;
import com.bankapp.models.Account;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Role;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ISystemManagerService;
import com.bankapp.services.IUserService;;;

/**
 * @author Nitesh Dhanpal
 *
 */

@Controller
@Secured("ROLE_MANAGER")
public class SystemManagerController implements Constants {


    @Autowired
    private ISystemManagerService manager;

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private IProfileRequestService profileReq;

    private final Logger LOGGER = Logger.getLogger(SystemManagerController.class);

    @RequestMapping(value = "/manager/criticaltransaction", method = RequestMethod.GET)
    public ModelAndView getCriticalTransaction() {
        List<Transaction> transactions = manager.getTransactionsByStatus(S_OTP_VERIFIED);
        ModelAndView mv = new ModelAndView();
        mv.addObject("critical", transactions);
        mv.setViewName("/manager/viewTransaction");
        return mv;
    }

    @RequestMapping(value = "/manager/pendingtransaction", method = RequestMethod.GET)
    public ModelAndView getInitiatedTransaction() {
        List<Transaction> transactions = manager.getTransactionsByStatus(S_PENDING);
        ModelAndView mv = new ModelAndView();
        mv.addObject("pending", transactions);
        mv.setViewName("/manager/viewPending");
        return mv;
    }

    @RequestMapping(value = "/manager/profilerequests", method = RequestMethod.GET)
    public ModelAndView getProfileRequests() {
        List<ProfileRequest> list = profileReq.getRequestsByStatus(S_PENDING);
        List<ProfileRequest> list1 = new ArrayList<>();
        ;
        int k = 0;
        for (int i = 0; i < list.size(); i++) {
            ProfileRequest Req = list.get(i);
            String role = Req.getRole().getName();
            if (role.equals("ROLE_CUSTOMER") || role.equals("ROLE_MERCHANT")) {
                list1.add(k, Req);
                k++;
            }
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("profileRequests", list1);
        mv.setViewName("/manager/profileRequests");
        return mv;
    }

    @RequestMapping(value = "/manager/approveprofilerequest", method = RequestMethod.POST)
    public ModelAndView approveprofilerequest(@ModelAttribute("row") ProfileRequest Id, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();

        ProfileRequest profile = manager.getProfilebRequestByRId(Id.getrId());
        String Address = profile.getAddress();
        Date DoB = profile.getDateOfBirth();
        String Ph = profile.getPhoneNumber();

        User user = profile.getUser();
        user.setAddress(Address);
        user.setDateOfBirth(DoB);
        user.setPhoneNumber(Ph);
        manager.saveUser(user);

        String status = manager.approveProfileRequest(profile);

        mv.addObject("message", new Message(status, "Action Completed"));
        // System.out.println("Done");
        mv.setViewName("/manager/profileRequests");

        return mv;
    }

    @RequestMapping(value = "/manager/getUserByEmail", method = RequestMethod.GET)
    public ModelAndView getuserEmail() {
        ModelAndView modelAndView = new ModelAndView("/manager/viewUserByEmailForm", "form", new ViewByEmailForm());
        return modelAndView;
    }

    @RequestMapping(value = "/manager/myaccount", method = RequestMethod.GET)
    public ModelAndView getmanagerhome(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        String Username = loggedInUser.getUsername();
        mv.addObject("username", Username);
        mv.setViewName("manager/myaccount");
        return mv;
    }

    @RequestMapping(value = "/manager/approvetransaction", method = RequestMethod.POST)
    public ModelAndView approvetransaction(@ModelAttribute("row") Transaction Id, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        // //System.out.println("Entered Approve");
        // //System.out.println("Transaction" + Id.getTransactionId());

        Transaction transaction = manager.getTransactionById(Id.getTransactionId());

        Account FromAccount = transaction.getFromAccount();
        Account ToAccount = transaction.getToAccount();
        Double AmountToBeSent = transaction.getAmount();
        // System.out.println(AmountToBeSent);

        Double FromAccountBalance = FromAccount.getBalance();
        // System.out.println(FromAccountBalance);

        String str = "";

        if (FromAccountBalance > AmountToBeSent) {
            manager.reflectChangesToSender(FromAccount, FromAccountBalance, AmountToBeSent);
            Double ToAccountBalance = ToAccount.getBalance();
            manager.reflectChangesToReceiver(ToAccount, ToAccountBalance, AmountToBeSent);
            str = manager.approveTransaction(transaction);
        } else {
            str = "Unsuccessfull";
        }
        // System.out.println(str);
        mv.addObject("result1", str);
        // System.out.println("Done");
        mv.setViewName("/manager/viewTransaction");

        return mv;
    }

    @RequestMapping(value = "/manager/addUserForm", method = RequestMethod.POST)
    public String addUser(final ModelMap model, @ModelAttribute("form") @Valid AddEmployeeForm form,
            BindingResult result, Errors errors, Principal principal, HttpServletRequest request,
            RedirectAttributes attributes) throws UserNameExistsException {
        User user = new User();
        Role role = form.getRole();
        String redirectUrl = "redirect:/manager/addUserForm";
        String status = "success";

        // System.out.println(form.getUsername());
        if (result.hasErrors()) {
            status = "error";
            model.addAttribute("form", form);
            String message = "Invalid Details";
            model.addAttribute("message", new Message(status, message));
            return "/manager/addUserForm";
        }

        User registered = null;

        user.setEmail(form.getEmail());
        user.setUsername(form.getUsername());
        user.setDateOfBirth(form.getDateOfBirth());

        String temporaryPassword = OneTimePassword.generateOTP();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        String message = "success";
        try {
            registered = userService.registerNewUserAccount(user, role.getName());
        } catch (EmailExistsException e1) {
            status = "error";
            message = String.format("Action: %s, Message: %s", "signup", e1.getMessage());
            LOGGER.error(message);
            redirectUrl = "redirect:/manager/addUserForm";

        }

        if (registered != null) {
            try {

                eventPublisher.publishEvent(
                        new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
            } catch (Exception e) {
                status = "error";
                message = String.format("Action: %s, Message: %s", "signup", e.getMessage());
                LOGGER.error(message);

            }

            userService.generateTemporaryPassword(registered);
        }

        // System.out.println("message out" + message) ;
        attributes.addFlashAttribute("message", new Message(status, message));
        return redirectUrl;

    }

    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    @RequestMapping(value = "/manager/viewUserByEmailForm", method = RequestMethod.POST)
    public ModelAndView getuser_byemail(final ModelAndView model, @ModelAttribute("form") @Valid ViewByEmailForm form,
            BindingResult result, Errors errors, Principal principal, RedirectAttributes attributes) {

        User user = null;
        String status = "success";
        String message = "";

        if (result.hasErrors()) {
            model.addObject("form", form);
            model.setViewName("/manager/viewUserByEmailForm");

            return model;
        }

        if (userService.emailExist(form.getEmail())) {
            try {
                user = manager.viewUserByEmail(form.getEmail());
            } catch (Exception e) {
                status = "error";
                message = String.format("Message: %s", e.getMessage());
                LOGGER.error(message);
            }

        } else {
            status = "error";
            message = String.format("Action: EmailDoesNotExist");
            // System.out.println(message);
            model.addObject("message", new Message(status, message));
            model.setViewName("/manager/viewUserByEmailForm");
            return model;

        }

        model.addObject("viewuser", user);
        model.setViewName("/manager/viewUser");

        return model;

    }

    @RequestMapping(value = "/manager/update", method = RequestMethod.GET)
    public ModelAndView customerAndMerchantDetails() {
        ModelAndView mv = new ModelAndView("/manager/update");
        List<User> customers = userService.getCustomers();
        List<User> merchants = userService.getMerchants();
        List<User> newList = new ArrayList<User>(customers);
        newList.addAll(merchants);
        UpdateUsersForm form = new UpdateUsersForm();
        form.setUsers(newList);
        mv.addObject("form", form);
        return mv;
    }

    @RequestMapping(value = "/manager/delete", method = RequestMethod.POST)
    public String deleteUser(@ModelAttribute("user") User user, BindingResult result, RedirectAttributes attributes) {
        userService.deleteUser(user);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        String msg = String.format("User '%s' has been deleted", user.getUsername());
        message.put("msg", msg);
        attributes.addFlashAttribute("message", message);
        return "redirect:/manager/update";
    }

    @RequestMapping(value = "/manager/update", method = RequestMethod.POST)
    public String updateustomerAndMerchantDetails(@ModelAttribute("user") User updatedUser, BindingResult result,
            RedirectAttributes attributes) {
        userService.updateUser(updatedUser.getId(), updatedUser);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        message.put("msg", "Details have been updated");
        attributes.addFlashAttribute("message", message);
        return "redirect:/manager/update";
    }

    @RequestMapping(value = "/manager/deleteUsers", method = RequestMethod.GET)
    public ModelAndView deleteUser() {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("/manager/deleteUser");
        List<User> users = userService.displayDeleteUsers();
        mv.addObject("viewuser", users);
        return mv;
    }

    @RequestMapping(value = "/manager/deleteUsers", method = RequestMethod.POST)
    public String deleteManager(@ModelAttribute("user") User user, BindingResult result,
            RedirectAttributes attributes) {
        Message message;
        userService.deleteExternalUser(user);
        String msg = String.format("Manager '%s' has been deleted", user.getUsername());
        message = new Message("succes", msg);
        attributes.addFlashAttribute("message", message);
        return "redirect:/manager/deleteUsers";
    }

    @RequestMapping(value = "/manager/addUserForm", method = RequestMethod.GET)
    public ModelAndView getUserAddage() {
        ModelAndView modelAndView = new ModelAndView("/manager/addUserForm", "form", new AddEmployeeForm());
        return modelAndView;
    }

}
