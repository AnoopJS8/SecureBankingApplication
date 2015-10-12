package com.bankapp.models;

import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class OneTimePassword {
	private static final int EXPIRATION = 60 * 24;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(targetEntity = Transaction.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "transaction_id")
	private Transaction transaction;

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private Date expiryDate;
	private boolean verified;

	public OneTimePassword() {
		super();
	}

	public OneTimePassword(Transaction transaction) {
		super();
		this.value = generateOTP();
		this.transaction = transaction;
		this.expiryDate = calculateExpiryDate(EXPIRATION);
		this.verified = false;
	}

	private Date calculateExpiryDate(int expiryTimeInMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		return new Date(cal.getTime().getTime());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public static int getExpiration() {
		return EXPIRATION;
	}

	public static String generateOTP() {
		String chars = "abcdefghijklmnopqrstuvwxyz"
				+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

		final int pw_len = 7;
		Random rnd = new SecureRandom();
		StringBuilder pass = new StringBuilder();
		for (int i = 0; i < pw_len; i++)
			pass.append(chars.charAt(rnd.nextInt(chars.length())));
		return pass.toString();
	}
}
