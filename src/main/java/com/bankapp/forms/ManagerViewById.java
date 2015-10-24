package com.bankapp.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


public class ManagerViewById {
	@NotNull
	Long Id;
	
	 public Long getId() {
	        return this.Id;
	    }

	    public void setId(Long Id) {
	        this.Id = Id;
	    }
}