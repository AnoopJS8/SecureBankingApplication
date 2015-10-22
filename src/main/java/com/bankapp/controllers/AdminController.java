package com.bankapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.forms.UpdateUsersForm;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.User;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.ITransactionService;
import com.bankapp.services.IUserService;

@Controller
@Secured("ROLE_ADMIN")
public class AdminController {

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
    public String updateManagerDetails(@ModelAttribute("user") User updatedManager, BindingResult result,
            RedirectAttributes attributes) {
        userService.updateUser(updatedManager.getId(), updatedManager);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        message.put("msg", "Manager details have been updated");
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/managers";
    }

    @RequestMapping(value = "/admin/managers/delete", method = RequestMethod.POST)
    public String deleteManager(@ModelAttribute("user") User user, BindingResult result,
            RedirectAttributes attributes) {
        userService.deleteUser(user);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        String msg = String.format("Manager '%s' has been deleted", user.getUsername());
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
    public String updateEmployeeDetails(@ModelAttribute("user") User updatedUser, BindingResult result,
            RedirectAttributes attributes) {
        userService.updateUser(updatedUser.getId(), updatedUser);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        message.put("msg", "Employee details have been updated");
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/employees";
    }

    @RequestMapping(value = "/admin/employees/delete", method = RequestMethod.POST)
    public String deleteEmployee(@ModelAttribute("user") User user, BindingResult result,
            RedirectAttributes attributes) {
        userService.deleteUser(user);
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        String msg = String.format("Employee '%s' has been deleted", user.getUsername());
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
    public String changeRequest(@ModelAttribute("request") ProfileRequest profileRequest, BindingResult result, RedirectAttributes attributes) {
        profileService.setRequestToVerified(profileRequest.getrId());
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        message.put("msg", "Profile request has been approved");
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/requests";
    }
    
    @RequestMapping(value = "/declineProfileRequest", method = RequestMethod.POST)
    public String declineRequest(@ModelAttribute("request") ProfileRequest profileRequest, BindingResult result, RedirectAttributes attributes) {
        profileService.setRequestToVerified(profileRequest.getrId());
        Map<String, String> message = new HashMap<String, String>();
        message.put("status", "success");
        message.put("msg", "Profile request has been Declined");
        attributes.addFlashAttribute("message", message);
        return "redirect:/admin/requests";
    }
}
