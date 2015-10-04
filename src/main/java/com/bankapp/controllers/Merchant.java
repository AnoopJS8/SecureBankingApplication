package com.bankapp.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Merchant {

	@RequestMapping(value = "/merchant", method = RequestMethod.GET)
	  public ModelAndView home() {
	      ModelAndView mv = new ModelAndView();
	      List<awesome> ab = new ArrayList<awesome>(); 
	      awesome a = new awesome();
	      a.setOne("Awesomeness a");
	      awesome b = new awesome();
	      b.setOne("Awesomeness b");
	      ab.add(a);
	      ab.add(b);
	      mv.addObject("hello", ab);
	      mv.setViewName("merchant");
	      return mv;
	  }
	
}

class awesome{
	private String one;

	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}
}
