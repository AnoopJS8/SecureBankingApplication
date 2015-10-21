package com.bankapp.forms;

import com.bankapp.models.Role;
import com.bankapp.models.User;

public class ManagerCreateUser extends RecaptchaForm {
   // User user;
    String email;
    String username;
    Role role;

    public ManagerCreateUser() {
     //   this.user = new User();
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

}
