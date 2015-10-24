package com.bankapp.forms;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;


public class ManagerViewByEmail {
	@NotBlank
    String email;
	
	 public String getEmail() {
	        return this.email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }
}