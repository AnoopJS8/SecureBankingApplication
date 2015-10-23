package com.bankapp.services;

import java.util.List;


import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Transaction;

public interface IProfileRequestService {
    
    public String saveProfileRequest(ProfileRequest profile);

	public List<ProfileRequest> getRequestsByStatus(String status);
	
	public ProfileRequest getRequestById(Long Id);

	public String authorizeRequest(ProfileRequest requests);
	

}
