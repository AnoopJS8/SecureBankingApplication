package com.bankapp.services;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.bankapp.validators.ValidEmail;
import com.bankapp.validators.ValidPassword;

public class UserDto {
    @NotNull
    @Size(min = 1)
    private String username;

    @ValidPassword
    private String password;

    @ValidEmail
    @NotNull
    @Size(min = 1)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    private Integer role;

    public Integer getRole() {
        return role;
    }

    public void setRole(final Integer role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [username=").append(username).append("]").append("[email").append(email).append("]").append("[password").append(password).append("]");
        return builder.toString();
    }
}
