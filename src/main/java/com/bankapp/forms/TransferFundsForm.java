package com.bankapp.forms;

import javax.validation.constraints.NotNull;

public class TransferFundsForm {
    @NotNull
    String email;

    @NotNull
    String amount;

    String comment;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
