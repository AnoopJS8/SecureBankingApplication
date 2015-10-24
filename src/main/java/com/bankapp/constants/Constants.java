package com.bankapp.constants;

public interface Constants {

	public String SUCCESS = "success";
	public String LESS_BALANCE = "lessbal";
	public String ERROR = "error";
	public String ERR_ACCOUNT_NOT_EXISTS = "Account does not exist!";
	public String CRITICAL = "Critical transaction";
	public String PII_EXISTS = "Pii already added";
	//Status
	public String S_PENDING = "P"; //pending for request like transfer money and profile
	public String S_OTP_PENDING = "OP"; //critical transaction
	public String S_VERIFIED = "V"; //normal request verified
	public String S_OTP_VERIFIED = "OV"; //critical transaction verified
	public String S_PENDING_CUSTOMER_VERIFICATION = "PCV"; //merchant request for customer money pending 
	public String S_CUSTOMER_VERIFIED = "CV"; //merchant request verified
	public String S_CUSTOMER_DECLINED = "CD"; //merchant request Declined
	public String S_PROFILE_UPDATE_PENDING = "PUP"; //Profile Changes pending
	public String S_PROFILE_UPDATE_VERFIED = "PUV";//profile changes verified
	public String S_PROFILE_UPDATE_DECLINED = "PUD";//profile changes decline
	public String S_PII_PENDING = "PIIP";//pii verification
	public String S_PII_AUTHORIZED = "PIIA";//pii verification
	public String S_PII_DECLINED = "PIID";//pii request pending
	public String S_PII_REQUEST_PENDING = "PIIRP";//pii request pending
	public String S_PII_REQUEST_DONE = "PIIRD";//pii request pending
	
	//OTP Resource Name
	public String R_TRANSACTION  = "TXN";
	public String R_ACCOUNT  = "ACC";
	public String R_USER  = "USER";
	
}