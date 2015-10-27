package com.bankapp.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.forms.AddEmployeeForm;
import com.bankapp.forms.EmployeeProfileUpdateForm;
import com.bankapp.forms.UpdateUsersForm;
import com.bankapp.models.PersonalIdentificationInfo;
import com.bankapp.models.PiiRequest;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.services.IPIIRequestService;
import com.bankapp.services.IPIIService;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
@Secured("ROLE_ADMIN")
public class AdminController implements Constants {

    private final Logger LOGGER = Logger.getLogger(AdminController.class);

    @Autowired
    IUserService userService;

    @Autowired
    ITransactionService transactionService;

    @Autowired
    IProfileRequestService profileService;

    @Autowired
    IPIIRequestService piiRequestService;

    @Autowired
    IPIIService piiService;

    @RequestMapping(value = "/admin/myaccount", method = RequestMethod.GET)
    public ModelAndView AdminDetails(Principal principal) {
        ModelAndView mv = new ModelAndView("/admin/myaccount");
        String logMessage = "";
        if (userService.hasMissingFields(principal)) {
            mv.addObject("message",
                    new Message("error", "You are missing important details. Please update your profile urgently"));
            logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "myaccount", "GET",
                    "admin", "error", "You are missing important details. Please update your profile urgently");
        }
        logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "myaccount", "GET",
                "admin", "success", "My Account");
        LOGGER.info(logMessage);
        return mv;
    }

    @RequestMapping(value = "/admin/managers", method = RequestMethod.GET)
    public ModelAndView managerDetails() {
        ModelAndView mv = new ModelAndView("/admin/managerDetails");
        List<User> managers = userService.getManagers();
        UpdateUsersForm form = new UpdateUsersForm();
        form.setUsers(managers);
        mv.addObject("form", form);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "managerDetails",
                "GET", "admin", "success", "My Account");
        LOGGER.info(logMessage);
        return mv;
    }

    @RequestMapping(value = "/admin/managers/update", method = RequestMethod.POST)
    public String updateManagerDetails(@Valid @ModelAttribute("user") EmployeeProfileUpdateForm updatedManager,
            BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            attributes.addFlashAttribute("message", new Message("error",
                    "Update details incorrect [Phone# should be 10 digits, and DoB in MM/dd/yyyy format]"));
            return "redirect:/admin/managers";
        }

        String status, message;
        User newUser = new User();
        newUser.setUsername(updatedManager.getUsername());
        newUser.setAddress(updatedManager.getAddress());
        newUser.setDateOfBirth(updatedManager.getDateOfBirth());
        newUser.setPhoneNumber(updatedManager.getPhoneNumber());

        String serviceStatus = userService.updateUser(updatedManager.getId(), newUser);

        switch (serviceStatus) {

        case SUCCESS:
            status = "success";
            message = "Employee details have been updated";
            break;
        case ERROR:
        default:
            status = "error";
            message = "Oops, something went wrong. Please try again!";
        }

        attributes.addFlashAttribute("message", new Message(status, message));

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "Update details",
                "POST", "admin", status, serviceStatus);
        LOGGER.info(logMessage);

        return "redirect:/admin/managers";
    }

    @RequestMapping(value = "/admin/managers/delete", method = RequestMethod.POST)
    public String deleteManager(@ModelAttribute("user") User user, BindingResult result,
            RedirectAttributes attributes) {
        userService.deleteUser(user);
        String msg = String.format("Manager '%s' has been deleted", user.getUsername());
        attributes.addFlashAttribute("message", new Message("success", msg));
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "managers/delete",
                "POST", "admin", "success", msg);
        LOGGER.info(logMessage);
        return "redirect:/admin/managers";
    }

    @RequestMapping(value = "/admin/employees", method = RequestMethod.GET)
    public ModelAndView employeeDetails() {
        ModelAndView mv = new ModelAndView("/admin/employeeDetails");
        List<User> employees = userService.getEmployees();
        UpdateUsersForm form = new UpdateUsersForm();
        form.setUsers(employees);
        mv.addObject("form", form);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "employee details",
                "GET", "admin", "success", "details");
        LOGGER.info(logMessage);
        return mv;
    }

    @RequestMapping(value = "/admin/employees/update", method = RequestMethod.POST)
    public String updateEmployeeDetails(@Valid @ModelAttribute("user") EmployeeProfileUpdateForm updatedUser,
            BindingResult result, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("message", new Message("error",
                    "Update details incorrect [Phone# should be 10 digits, and DoB in MM/dd/yyyy format]"));
            return "redirect:/admin/employees";
        }

        String status, message;
        User newUser = new User();
        newUser.setUsername(updatedUser.getUsername());
        newUser.setAddress(updatedUser.getAddress());
        newUser.setDateOfBirth(updatedUser.getDateOfBirth());
        newUser.setPhoneNumber(updatedUser.getPhoneNumber());

        String serviceStatus = userService.updateUser(updatedUser.getId(), newUser);

        switch (serviceStatus) {

        case SUCCESS:
            status = "success";
            message = "Employee details have been updated";
            break;
        case ERROR:
        default:
            status = "error";
            message = "Oops, something went wrong. Please try again!";
        }

        attributes.addFlashAttribute("message", new Message(status, message));

        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "Update details",
                "POST", "admin", status, serviceStatus);
        LOGGER.info(logMessage);

        return "redirect:/admin/employees";
    }

    @RequestMapping(value = "/admin/employees/delete", method = RequestMethod.POST)
    public String deleteEmployee(@ModelAttribute("user") User user, BindingResult result,
            RedirectAttributes attributes) {
        userService.deleteUser(user);
        String msg = String.format("Employee '%s' has been deleted", user.getUsername());
        Message message = new Message("success", "Employee deleted successfully");
        attributes.addFlashAttribute("message", message);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "delete employee",
                "POST", "admin", "success", msg);
        LOGGER.info(logMessage);
        return "redirect:/admin/employees";
    }

    @RequestMapping(value = "/admin/requests", method = RequestMethod.GET)
    public ModelAndView profileRequest() {
        ModelAndView mv = new ModelAndView("/admin/profileRequest");
        List<ProfileRequest> requests = profileService.getPendingRequests();
        mv.addObject("requests", requests);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "Profile Requests",
                "GET", "admin", "success", "profile request");
        LOGGER.info(logMessage);
        return mv;
    }

    @RequestMapping(value = "/approveProfileRequest", method = RequestMethod.POST)
    public String changeRequest(@ModelAttribute("request") ProfileRequest profileRequest, BindingResult result,
            RedirectAttributes attributes) {
        Message message;
        ProfileRequest request = profileService.getRequestById(profileRequest.getrId());
        profileService.authorizeRequest(request);
        message = new Message("success", "Request has been approved");
        attributes.addFlashAttribute("message", message);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]",
                "approve Profile Request", "POST", "admin", "success", message);
        LOGGER.info(logMessage);
        return "redirect:/admin/requests";
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.GET)
    public ModelAndView addEmployee() {
        ModelAndView mv = new ModelAndView("admin/addemployee");
        AddEmployeeForm form = new AddEmployeeForm();
        mv.addObject("form", form);
        return mv;
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.POST)
    public String addUser(final ModelMap model, @ModelAttribute("form") @Valid AddEmployeeForm form,
            BindingResult result, RedirectAttributes attr) {

        String logMessage = String.format("Action: %s, Message: %s", "addEmployee", "POST");
        String redirectSuccessURL = "redirect:/admin/myaccount";
        String redirectFailureURL = "redirect:/admin/add";

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "admin/addemployee";
        }

        User user = new User();
        user.setEmail(form.getEmail());
        user.setUsername(form.getUsername());
        user.setDateOfBirth(form.getDateOfBirth());
        Role role = form.getRole();
        try {
            String msg = userService.addEmployee(user, role.getName());
            if (msg.equalsIgnoreCase("EmailExists")) {
                attr.addFlashAttribute("message", new Message("error", "Email already exists"));
                return redirectFailureURL;
            }
            if (msg.equalsIgnoreCase("mailError")) {
                attr.addFlashAttribute("message",
                        new Message("error", "Mail service dint work properly please try again"));
                return redirectFailureURL;
            }
            attr.addFlashAttribute("message", new Message("success", "Internal user has been created"));
            logMessage = String.format("Action: %s, Message: %s", "add", "Employee has been created");
            return redirectSuccessURL;
        } catch (EmailExistsException e) {
            logMessage = String.format("Action: %s, Message: %s", "email_exists", e.getMessage());
            attr.addFlashAttribute("message", new Message("error", "Email already exists"));
            return redirectFailureURL;
        } finally {
            LOGGER.info(logMessage);
        }
    }

    @RequestMapping(value = "/declineProfileRequest", method = RequestMethod.POST)
    public String declineRequest(@ModelAttribute("request") ProfileRequest profileRequest, BindingResult result,
            RedirectAttributes attributes) {
        Message message;
        ProfileRequest request = profileService.getRequestById(profileRequest.getrId());
        String msg = profileService.declineRequest(request);
        if (msg.equals(ERROR)) {
            message = new Message("error", "Please try again");
            attributes.addFlashAttribute("message", message);
            String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]",
                    "declineProfileRequest", "POST", "admin", "error", "Error");
            LOGGER.info(logMessage);
            return "redirect:/admin/requests";
        }
        message = new Message("success", "Request has been declined");
        attributes.addFlashAttribute("message", message);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]",
                "declineProfileRequest", "POST", "admin", "success", "Declined Profile Request");
        LOGGER.info(logMessage);
        return "redirect:/admin/requests";

    }

    @RequestMapping(value = "/admin/piirequest", method = RequestMethod.GET)
    public ModelAndView piiRequest() {
        ModelAndView mv = new ModelAndView("/admin/piirequest");
        mv.addObject("role", "admin");
        PiiRequest piiRequest = new PiiRequest();
        mv.addObject("piirequest", piiRequest);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "piirequest", "GET",
                "admin", "success", "Pii details");
        LOGGER.info(logMessage);
        return mv;
    }

    @RequestMapping(value = "/admin/piirequest", method = RequestMethod.POST)
    public String savePiiRequest(@ModelAttribute("piirequest") PiiRequest piiRequest, BindingResult result,
            RedirectAttributes attributes) {

        Message message;
        String msg = piiRequestService.saveRequest(piiRequest);
        String status = "";
        if (msg.equals(SUCCESS)) {
            message = new Message("success", "Request has been forwarded to agency ");
            status = "sucesss";
        } else if (msg.equals(ERR_EMAIL_NOT_EXISTS)) {
            message = new Message("error", "Email does not exist");
            status = "error";
        } else {
            message = new Message("error", "error please try again");
            status = "error";
        }
        attributes.addFlashAttribute("message", message);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "piirequest",
                "POST", "admin", status, message);
        LOGGER.info(logMessage);
        return "redirect:/admin/piirequest";

    }

    @RequestMapping(value = "/admin/piidetails", method = RequestMethod.GET)
    public ModelAndView piiDetails() {
        ModelAndView mv = new ModelAndView("/admin/piidetails");
        mv.addObject("role", "admin");
        List<PersonalIdentificationInfo> piiList = piiService.getAuthorizedPII();
        mv.addObject("piiInfo", piiList);
        String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "piidetails", "GET",
                "admin", "success", "Pii details");
        LOGGER.info(logMessage);
        return mv;
    }

    @RequestMapping(value = "/admin/logs", method = RequestMethod.GET)
    public ModelAndView logDetails() {

        File file = new File("logging.log");
        Scanner in = null;
        List<String> l1 = new ArrayList<String>();
        int count = 0;
        try {
            in = new Scanner(file);
            while (in.hasNext() && count < 51) {
                String line = in.nextLine();
                if ((line.contains("WARN") || line.contains("ERROR"))) {
                    count++;
                    l1.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            String logMessage = String.format("[Action=%s, Method=%s, Role=%s][Status=%s][Message=%s]", "admin view log", "GET",
                    "admin", "error", "View Logs");
            LOGGER.info(logMessage);
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("listlogs", l1);
        mv.setViewName("/admin/viewLogs");
        return mv;

    }

}
