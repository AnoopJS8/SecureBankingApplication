package com.bankapp.services;

import java.util.List;

import com.bankapp.models.ProfileRequest;

public interface IProfileRequestService {
    
    public String saveProfileRequest(ProfileRequest profile);

    List<ProfileRequest> findStatus(String status);
    
    void changerequest(Long id);
    
}
