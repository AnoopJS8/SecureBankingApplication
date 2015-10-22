package com.bankapp.forms;

import com.bankapp.models.Role;
import com.bankapp.models.User;

public class AddEmployeeForm {
    User user;
    Role role;

    public AddEmployeeForm() {
        this.user = new User();
        this.role = new Role();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
