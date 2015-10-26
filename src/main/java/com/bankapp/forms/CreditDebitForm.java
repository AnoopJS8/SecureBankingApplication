package com.bankapp.forms;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class CreditDebitForm {

    @NotNull
    String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NotBlank
    String status;

}
