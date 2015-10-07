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
@Table(name = "transactions")
public class Transaction {

	 @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
     private Long transactionId;
	 
	 @ManyToOne() 
	 @JoinColumn(name = "fromAccId", nullable=false)
	 private Account fromAccount;
	 
	 @ManyToOne() 
	 @JoinColumn(name = "userId" , nullable=false)
	 private User user;
	 
	 private String comment;
	 
	 @ManyToOne() 
	 @JoinColumn(name= "toAccId" , nullable=false)
	 private Account toAccount;
	 
	 @NotNull
	 private Double amount;
	 
	 @NotNull
	 private String type;
	 
	 private Date created;
	 private Date updated;

	  @PrePersist
	  protected void onCreate() {
	    created = new Date();
	  }
	
	  @PreUpdate
	  protected void onUpdate() {
	    updated = new Date();
	  }

	
	  
	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Account getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Account getToAccount() {
		return toAccount;
	}

	public void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	 
	
	 
	 
}
