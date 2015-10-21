package com.bankapp.services;


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
   
    
}
