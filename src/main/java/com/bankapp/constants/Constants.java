
package com.bankapp.constants;

public interface Constants {

	public String SUCCESS = "success";
	public String LESS_BALANCE = "lessbal";
	public String ERROR = "error";
	public String ERR_ACCOUNT_NOT_EXISTS = "Account does not exist!";
	public String CRITICAL = "Critical transaction";
	//Status
	public String S_PENDING = "P"; //pending for request like transfer money and profile
	public String S_OTP_PENDING = "OP"; //critical transaction
	public String S_VERIFIED = "V"; //normal request verified
	public String S_OTP_VERIFIED = "OV"; //critical transaction verified
	public String S_PENDING_CUSTOMER_VERIFICATION = "PCV"; //merchant request for customer money pending 
	public String S_CUSTOMER_VERIFIED = "CV"; //merchant request verified
	public String S_PROFILE_UPDATE_PENDING = "PUP"; //Profile Changes pending
	public String S_PROFILE_UPDATE_VERFIED = "PUV";//profile changes verified
	public String S_PROFILE_UPDATE_DECLINED = "PUD";//profile changes verified
	//OTP Resource Name
	public String R_TRANSACTION  = "TXN";
	public String R_ACCOUNT  = "ACC";
	public String R_USER  = "USER";
	
}
