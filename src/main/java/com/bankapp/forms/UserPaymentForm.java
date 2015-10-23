package com.bankapp.forms;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UserPaymentForm {

    @NotNull
    Long accountId;

    @NotNull
    @Min(value = 0)
    Double amount;

    String comment;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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
