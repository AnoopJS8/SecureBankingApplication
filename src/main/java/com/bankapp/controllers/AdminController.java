package com.bankapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.exceptions.UserAlreadyExistException;
import com.bankapp.exceptions.UserNameExistsException;
import com.bankapp.forms.AddEmployeeForm;
import com.bankapp.forms.UpdateUsersForm;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
@Secured("ROLE_ADMIN")
public class AdminController {

    private final Logger LOGGER = Logger
            .getLogger(SystemManagerController.class);

    @Autowired
    IUserService userService;

    @Autowired
    ITransactionService transactionService;

    @Autowired
    IProfileRequestService profileService;

    @RequestMapping(value = "/admin/myaccount", method = RequestMethod.GET)
    public ModelAndView AdminDetails() {
        ModelAndView mv = new ModelAndView("/admin/myaccount");
        return mv;
    }

    @RequestMapping(value = "/admin/managers", method = RequestMethod.GET)
    public ModelAndView managerDetails() {
        ModelAndView mv = new ModelAndView("/admin/managerDetails");
        List<User> managers = userService.getManagers();
        UpdateUsersForm form = new UpdateUsersForm();
        form.setUsers(managers);
        mv.addObject("form", form);
        return mv;
    }

    @RequestMapping(value = "/admin/managers/update", method = RequestMethod.POST)
    public String updateManagerDetails(
            @ModelAttribute("user") User updatedManager, BindingResult result,
            RedirectAttributes attributes) {
        userService.updateUser(updatedManager.getId(), updatedManager);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        message.put("msg", "Manager details have been updated");
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/managers";
    }

    @RequestMapping(value = "/admin/managers/delete", method = RequestMethod.POST)
    public String deleteManager(@ModelAttribute("user") User user,
            BindingResult result, RedirectAttributes attributes) {
        userService.deleteUser(user);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        String msg = String.format("Manager '%s' has been deleted",
                user.getUsername());
        message.put("msg", msg);
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/managers";
    }

    @RequestMapping(value = "/admin/employees", method = RequestMethod.GET)
    public ModelAndView employeeDetails() {
        ModelAndView mv = new ModelAndView("/admin/employeeDetails");
        List<User> employees = userService.getEmployees();
        UpdateUsersForm form = new UpdateUsersForm();
        form.setUsers(employees);
        mv.addObject("form", form);
        return mv;
    }

    @RequestMapping(value = "/admin/employees/update", method = RequestMethod.POST)
    public String updateEmployeeDetails(
            @ModelAttribute("user") User updatedUser, BindingResult result,
            RedirectAttributes attributes) {
        userService.updateUser(updatedUser.getId(), updatedUser);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        message.put("msg", "Employee details have been updated");
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/employees";
    }

    @RequestMapping(value = "/admin/employees/delete", method = RequestMethod.POST)
    public String deleteEmployee(@ModelAttribute("user") User user,
            BindingResult result, RedirectAttributes attributes) {
        userService.deleteUser(user);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        String msg = String.format("Employee '%s' has been deleted",
                user.getUsername());
        message.put("msg", msg);
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/employees";
    }

    @RequestMapping(value = "/admin/requests", method = RequestMethod.GET)
    public ModelAndView profileRequest() {
        ModelAndView mv = new ModelAndView("/admin/profileRequest");
        List<ProfileRequest> requests = profileService.getPendingRequests();
        mv.addObject("requests", requests);
        return mv;
    }

    @RequestMapping(value = "/approveProfileRequest", method = RequestMethod.POST)
    public String ChangeRequest(@ModelAttribute("rId") Long rid,
            BindingResult result, RedirectAttributes attributes) {
        profileService.setRequestToVerified(rid);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        message.put("msg", "Profile request has been approved");
        attributes.addFlashAttribute("message", message);
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
    public String addUser(@ModelAttribute("form") AddEmployeeForm form, RedirectAttributes attr) {

        String logMessage = String.format("Action: %s, Message: %s",
                "addEmployee", "POST");
        String redirectSuccessURL = "redirect:/admin/myaccount";
        String redirectFailureURL = "redirect:/admin/add";

        User user = form.getUser();
        Role role = form.getRole();
        try {
            user = userService.addEmployee(user, role.getName());
            attr.addFlashAttribute("message", new Message("success", "Employee has been created"));
            return redirectSuccessURL;
        } catch (EmailExistsException e) {
            logMessage = String.format("Action: %s, Message: %s",
                    "email_exists", e.getMessage());
            attr.addFlashAttribute("message", new Message("error", "Email already exists"));
            return redirectFailureURL;
        } finally {
            LOGGER.error(logMessage);
        }
    }

}
