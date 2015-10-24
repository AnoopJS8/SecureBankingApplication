package com.bankapp.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String accId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @NotNull
    private Double balance;
    private Date created;
    private Date updated;

    @Min(value = 0)
    private Double criticalLimit;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }

    public Account() {
        super();
    }

    public Account(final User user, final Double balance, final Double criticalLimit) {
        super();
        this.user = user;
        this.balance = balance;
        this.criticalLimit = criticalLimit;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Double getCriticalLimit() {
        return criticalLimit;
    }

    public void setCriticalLimit(Double criticalLimit) {
        this.criticalLimit = criticalLimit;
    }

    @Override
    public String toString() {
        final String value = String.format("Account [userId=%s, accId=%s, balance=%s, criticalLimit=%s]", user.getId(),
                accId, balance, criticalLimit);
        return value;
    }

}
