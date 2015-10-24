package com.bankapp.controllers;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.forms.OTPForm;
import com.bankapp.forms.ProfileForm;
import com.bankapp.listeners.OnOtpEvent;
import com.bankapp.models.Account;
import com.bankapp.models.PersonalIdentificationInfo;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.User;
import com.bankapp.services.IAccountService;
import com.bankapp.services.IPIIService;
import com.bankapp.services.IProfileRequestService;
import com.bankapp.services.IUserService;

@Controller
public class MainController implements Constants {

    private final Logger LOGGER = Logger.getLogger(MainController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IProfileRequestService profileRequestService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private IPIIService ipiiservice;
    
    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        CustomDateEditor editor = new CustomDateEditor(sdf, false);
        binder.registerCustomEditor(Date.class, editor);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        mv.setViewName("index");
        if (loggedInUser != null) {
            mv.addObject("lastLoginDate", loggedInUser.getLastLoginDate());
            mv.addObject("lastLoginIP", loggedInUser.getLastLoginIP());
        }
        return mv;
    }

    @RequestMapping(value = "/login/identify", method = RequestMethod.GET)
    public ModelAndView getRecoverPassword() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("registration/recoverPassword");
        mv.addObject("email", "");
        return mv;
    }

    @RequestMapping(value = "/login/identify", method = RequestMethod.POST)
    public ModelAndView postRecoverPassword(@ModelAttribute("email") String email) {
        String logMessage = String.format("Password recovery request for user [email=%s]", email);
        LOGGER.info(logMessage);

        User registeredUser = userService.getUserByEmail(email);
        ModelAndView mv = new ModelAndView();

        if (registeredUser == null) {
            String message = String.format("Sorry, we could not find any user with the email '%s'", email);
            mv.setViewName("error");
            mv.addObject("message", message);
            return mv;
        } else {
            mv.setViewName("registration/verifySecurityQuestion");
            mv.addObject("email", email);
            mv.addObject("securityQuestion", registeredUser.getSecurityQuestion());
            mv.addObject("securityAnswer", "");
            return mv;
        }
    }

    @RequestMapping(value = "/login/verifyIdentity", method = RequestMethod.POST)
    public ModelAndView verifySecurityAnswer(@ModelAttribute("email") String email, BindingResult bindingEmail,
            @ModelAttribute("securityQuestion") String question, BindingResult bindingQuestionResult,
            @ModelAttribute("securityAnswer") String answer, BindingResult bindingAnswerResult) {
        String logMessage = String.format("Verifying user email %s with [Question: %s, Answer: %s]", email, question,
                answer);
        LOGGER.info(logMessage);

        User registeredUser = userService.getUserByEmail(email);
        ModelAndView mv = new ModelAndView();

        if (registeredUser == null) {
            String message = String.format("Sorry, we could not find any user with the email '%s'", email);
            mv.setViewName("error");
            mv.addObject("message", message);
            return mv;
        } else {
            String expectedAnswer = registeredUser.getSecurityAnswer();
            if (expectedAnswer.equalsIgnoreCase(answer)) {
                String message = String.format(
                        "Thank you for answering your security question. We have sent an email with a temporary password to %s",
                        email);
                mv.setViewName("success");
                mv.addObject("message", message);
                userService.generateTemporaryPassword(registeredUser);
                return mv;
            } else {
                String message = String
                        .format("Sorry, we could not verify the answer you specified. Please try again.");
                mv.setViewName("error");
                mv.addObject("message", message);
                return mv;
            }
        }
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile(Principal principal) {

        ModelAndView mv = new ModelAndView("profile");
        ProfileForm form = new ProfileForm();

        User loggedInUser = userService.getUserFromSession(principal);
        String role = loggedInUser.getRole().getName();
        form.setUsername(loggedInUser.getUsername());
        form.setEmail(loggedInUser.getEmail());
        form.setAddress(loggedInUser.getAddress());
        form.setDateOfBirth(loggedInUser.getDateOfBirth());

        mv.addObject("form", form);
        mv.addObject("role", role);

        return mv;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String updateProfile(final ModelMap model, @ModelAttribute("form") @Valid ProfileForm form,
            BindingResult result, Principal principal, RedirectAttributes attributes) {

        String status;
        String message;
        String redirectUrl;
        String logMessage;

        if (result.hasErrors()) {
            model.addAttribute("form", form);
            return "profile";
        }

        ProfileRequest profile = new ProfileRequest();
        profile.setAddress(form.getAddress());
        profile.setDateOfBirth(form.getDateOfBirth());
        profile.setPhoneNumber(form.getPhoneNumber());
        profile.setStatus(S_PROFILE_UPDATE_PENDING);
        profile.setUser(userService.getUserFromSession(principal));
        profile.setRole(userService.getUserFromSession(principal).getRole());

        String serviceStatus = profileRequestService.saveProfileRequest(profile);

        if (serviceStatus.equalsIgnoreCase(ERROR)) {
            status = "error";
            message = "Error Occurred";
            redirectUrl = "redirect:/profile";
        } else {
            status = "success";
            message = "Request for changes are sent to out employee";
            redirectUrl = "redirect:/profile";
        }

        attributes.addFlashAttribute("message", new Message(status, message));
        attributes.addFlashAttribute("role", userService.getUserFromSession(principal).getRole().getName());

        logMessage = String.format("[Action=%s, Method=%s][Status=%s][Message=%s]", "transferfunds", "POST",
                serviceStatus, message);
        LOGGER.info(logMessage);

        return redirectUrl;

    }

    @RequestMapping(value = "/changepassword", method = RequestMethod.GET)
    public ModelAndView changePassword(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        mv.addObject("user", loggedInUser);
        mv.addObject("role", loggedInUser.getRole().getName());
        mv.setViewName("changepassword");
        return mv;
    }

    @RequestMapping(value = "/changepassword", method = RequestMethod.POST)
    public ModelAndView otpVerification(@ModelAttribute("user") @Valid User user, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        mv.addObject("user", loggedInUser);
        mv.addObject("role", loggedInUser.getRole().getName());
        boolean checkPassword = userService.verifyPassword(loggedInUser, user.getPassword());
        if (checkPassword) {
            loggedInUser.setNewpassword(user.getNewpassword());
            userService.saveRegisteredUser(loggedInUser);
            try {
                eventPublisher.publishEvent(new OnOtpEvent(loggedInUser.getId(), R_USER));
            } catch (Exception e) {
                String message = String.format("Action: %s, Message: %s", "change password", e.getMessage());
                LOGGER.info(message);
                e.printStackTrace();
                mv.addObject("message", e.getMessage());
                mv.setViewName("error");
                return mv;
            }
            OTPForm enteredValue = new OTPForm();
            mv.addObject("otp", enteredValue);
            mv.setViewName("otp");
        } else {
            mv.addObject("message", "Wrong password");
            mv.setViewName("changepassword");
        }
        return mv;
    }

    @RequestMapping(value = "/otp", method = RequestMethod.POST)
    public String otpVerification(@Valid @ModelAttribute("otp") OTPForm otp, BindingResult result, WebRequest request,
            Errors errors, Principal principal, RedirectAttributes attributes) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        mv.addObject("user", loggedInUser);
        mv.addObject("role", loggedInUser.getRole().getName());
        boolean checkOtp = userService.verifyOTP(otp.getOtp(), loggedInUser.getId(), R_USER);
        if (checkOtp) {
            userService.changePassword(loggedInUser);
            attributes.addFlashAttribute("message", new Message("success", "Password updated successfully"));
            attributes.addFlashAttribute("role", userService.getUserFromSession(principal).getRole().getName());
        } else {
            attributes.addFlashAttribute("message", new Message("error", "error in updating password"));
            attributes.addFlashAttribute("role", userService.getUserFromSession(principal).getRole().getName());
        }
        return "redirect:/profile";
    }

    @RequestMapping(value = "/changelimit", method = RequestMethod.POST)
    public String changeLimit(@ModelAttribute("limit") @Valid Double newLimit, BindingResult result, WebRequest request,
            Errors errors, Principal principal, RedirectAttributes attributes) {

        User loggedInUser = userService.getUserFromSession(principal);
        if (newLimit < 0) {
            attributes.addFlashAttribute("message", new Message("error", "Critical limit cannot be set below 0"));
        } else {

            Account newAccount = accountService.getAccountByUser(loggedInUser);
            newAccount.setCriticalLimit(newLimit);
            accountService.saveAccount(newAccount);
            attributes.addFlashAttribute("message", new Message("success", "Your critical limit has been updated"));
        }

        String roleName = loggedInUser.getRole().getName();
        if (roleName.equalsIgnoreCase("ROLE_CUSTOMER")) {
            return "redirect:/customer/myaccount";
        } else {
            return "redirect:/merchant/myaccount";
        }
    }

    @RequestMapping(value = "/pii", method = RequestMethod.GET)
    public ModelAndView addPII(Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        PersonalIdentificationInfo pii = new PersonalIdentificationInfo();
        mv.addObject("role", loggedInUser.getRole().getName());
        mv.addObject("pii", pii);
        mv.setViewName("pii");
        return mv;
    }

    @RequestMapping(value = "/pii", method = RequestMethod.POST)
    public ModelAndView savePII(@ModelAttribute("pii") @Valid PersonalIdentificationInfo pii, BindingResult result,
            WebRequest request, Errors errors, Principal principal) {
        ModelAndView mv = new ModelAndView();
        User loggedInUser = userService.getUserFromSession(principal);
        pii.setEmail(loggedInUser.getEmail());
        pii.setStatus(S_PII_PENDING);
        String message = ipiiservice.savePII(pii);
        if (message.equals(SUCCESS)) {
            mv.addObject("message", "Pii added successfully");
            mv.setViewName("success");
        } else {
            mv.addObject("message", "Error in adding the pii please try again");
            mv.setViewName("error");
        }
        return mv;
    }
}