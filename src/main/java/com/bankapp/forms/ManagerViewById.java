package com.bankapp.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;


public class ManagerViewById {
	@NotEmpty
	String Id;
	
	 public String getId() {
	        return this.Id;
	    }

	    public void setId(String Id) {
	        this.Id = Id;
	    }
}