package com.bankapp.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Constants;
import com.bankapp.constants.Message;
import com.bankapp.models.PersonalIdentificationInfo;
import com.bankapp.models.PiiRequest;
import com.bankapp.services.IPIIRequestService;
import com.bankapp.services.IPIIService;

@Controller
@Secured("ROLE_AGENCY")
public class AgencyController implements Constants{
    
    @Autowired
    IPIIRequestService piiRequestService;
    
    @Autowired
    IPIIService piiService;
    
    @RequestMapping(value = "/agency/myaccount", method = RequestMethod.GET)
    public ModelAndView AdminDetails() {
        ModelAndView mv = new ModelAndView("/agency/myaccount");
        return mv;
    }
    
    @RequestMapping(value = "/agency/adminrequests", method = RequestMethod.GET)
    public ModelAndView adminRequests() {
        ModelAndView mv = new ModelAndView("/agency/adminrequests");
        mv.addObject("role", "agency");
        List<PiiRequest> piiRequestList = piiRequestService.getPiiAdminRequest();
        mv.addObject("piiRequest", piiRequestList);
        return mv;
    }
    
    @RequestMapping(value = "/agency/approvePIIRequest", method = RequestMethod.POST)
    public String approveDetails(@ModelAttribute("request") PiiRequest request, BindingResult result,
            RedirectAttributes attributes) {
        String status;
        String message;
        String msg = piiRequestService.authorize(request);
        if(msg.equals(SUCCESS)){
            status = "success";
            message = "Request has been authorized";
        }else{
            status = "error";
            message = "Request has been authorized";
        }
        attributes.addFlashAttribute("message", new Message(status, message));
        return "redirect:/agency/adminrequests";
    }
    
    @RequestMapping(value = "/agency/declinePIIRequest", method = RequestMethod.POST)
    public String declineDetails(@ModelAttribute("request") PiiRequest request, BindingResult result,
            RedirectAttributes attributes) {
        Message message;
        String msg = piiRequestService.decline(request);
        if(msg.equals(SUCCESS)){
            message = new Message("succes", "Request has been declined");
        }else{
            message = new Message("error", "error please try again");
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/agency/adminrequests";
        
    }
    
    @RequestMapping(value = "/agency/piidetails", method = RequestMethod.GET)
    public ModelAndView piiDetails() {
        ModelAndView mv = new ModelAndView("/agency/piidetails");
        mv.addObject("role", "agency");
        List<PersonalIdentificationInfo> piiList = piiService.getPiiInfo();
        mv.addObject("piiInfo", piiList);
        return mv;
    }

}
