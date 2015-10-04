package com.bankapp.models;

import java.util.Date;

import javax.persistence.CascadeType;
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
     private Long tId;
	 
	 @ManyToOne(cascade=CascadeType.ALL) 
	 @JoinColumn(name = "accId", nullable=false)
	 private Account account;
	 
	 @ManyToOne(cascade=CascadeType.ALL) 
	 @JoinColumn(name = "userId" , nullable=false)
	 private User user;
	 
	 private String comment;
	 
	 @ManyToOne(cascade=CascadeType.ALL) 
	 @JoinColumn(name= "toAccId" , nullable=false)
	 private Account toccount;
	 
	 @NotNull
	 private Double amount;
	 
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

	public Long gettId() {
		return tId;
	}

	public void settId(Long tId) {
		this.tId = tId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
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

	public Account getToccount() {
		return toccount;
	}

	public void setToccount(Account toccount) {
		this.toccount = toccount;
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

	  
	
	 
	 
}
