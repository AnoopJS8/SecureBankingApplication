package com.bankapp.forms;



import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

public class InitiateTransactionForm {

    @NotNull
    Long accountId;
    
    @NotNull
    Double amount;
    
    String comment;
    
    @NotNull
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @Future
    Date transferDate;

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

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }
}
