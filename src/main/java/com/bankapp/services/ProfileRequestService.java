package com.bankapp.services;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.ProfileRequest;
import com.bankapp.repositories.ProfileRequestRepository;

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
   
    public List<ProfileRequest> findStatus(String status)
    {
    	
    	List<ProfileRequest> request1 = new ArrayList(); 
    	List<ProfileRequest> request = profileRequestRepository.findByStatus(status);
    	for(int i=0;i<request.size();i++)
    	{
    		if(request.get(i).getUser().getRole().getId()==1)
    		{
    			request1.add(request.get(i));
    		}
    	}
    	
    	
    	return request1;
    	
    	
    }

	@Override
	public void changerequest(Long id) {
		// TODO Auto-generated method stub
		
		ProfileRequest profile = profileRequestRepository.findByRId(id);
		profile.setStatus("verified");
		profileRequestRepository.save(profile);
		
	}
    
}
