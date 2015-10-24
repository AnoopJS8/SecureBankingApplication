package com.bankapp.forms;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.bankapp.models.Role;

public class ManagerCreateUser {
	@NotBlank
    String email;
	
	@NotBlank
    String username;

	Role role;

    public ManagerCreateUser() {
        this.role = new Role();
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
    	return this.email;
    }
}
