package com.bankapp.forms;

import org.hibernate.validator.constraints.NotBlank;

public class ViewByEmailForm {
    @NotBlank
    String email;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}