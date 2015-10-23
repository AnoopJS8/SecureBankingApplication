package com.bankapp.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.ProfileRequest;
import com.bankapp.constants.Constants;
import com.bankapp.models.Transaction;
import com.bankapp.repositories.ProfileRequestRepository;
import com.bankapp.services.IProfileRequestService;

@Service
public class ProfileRequestService implements IProfileRequestService, Constants{

    @Autowired
    private ProfileRequestRepository profileRequestRepository;
    
 
    
    @Transactional
    @Override
    public String saveProfileRequest(ProfileRequest profile) {
        try{
            profileRequestRepository.save(profile);
            return SUCCESS; 
        }catch(Exception e){
            return ERROR;
        }
        
    }
    

    
	@Override
	public List<ProfileRequest> getRequestsByStatus(String status) {
		// TODO Auto-generated method stub
		
		Long merchant = new Long(3);
		Long customer = new Long(4);
		
		List<ProfileRequest> list=profileRequestRepository.findByStatusAndRoleId(status,merchant);
		
		list.addAll(profileRequestRepository.findByStatusAndRoleId(status, customer));
		return list;
	}
   
	@Override
	    public ProfileRequest getRequestById(Long id) {
		 
	        ProfileRequest request = profileRequestRepository.findOne(id);
	        return request;
	    }


	    public String authorizeRequest(ProfileRequest requests) {

	        String result = "";
	        
	        requests.setStatus("PUV");

	        try {
	        	profileRequestRepository.save(requests);
	            result = "Profile modification has been approved";
	            System.out.println("Transaction has been approved");
	        } catch (Exception e) {
	            result = "unsuccessull";
	        }

	        return result;
	    }

		@Override
		public String declineRequest(ProfileRequest requests) {
			// TODO Auto-generated method stub
			String result = "";
			 requests.setStatus("PUD");

		        try {
		        	profileRequestRepository.save(requests);
		            result = "Profile modification has been declined";
		            System.out.println("Transaction has been Declined");
		        } catch (Exception e) {
		            result = "unsuccessull";
		        }

		        return result;
		}
    
	    
	    
  
	    
}
