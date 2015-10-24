package com.bankapp.forms;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TransferFundsForm {
    @NotNull
    String email;

    @NotNull
    @Min(value = 0)
    Double amount;

    String comment;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
