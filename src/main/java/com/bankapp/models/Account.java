package com.bankapp.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "accounts")
public class Account {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long accId;
	
	@ManyToOne
	@JoinColumn(name="userId" , nullable=false)
	private User user;
	
	@NotNull
	private Double balance;
	private Date created;
	private Date updated;
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

	public Long getAccId() {
		return accId;
	}

	public void setAccId(Long accId) {
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
        final StringBuilder builder = new StringBuilder();
        builder.append("Account [userid=").append(user.getUsername()).append("]").append("[accId=").append(accId).append("]").append("[balance=").append(balance).append("]");
        return builder.toString();
    }

}
