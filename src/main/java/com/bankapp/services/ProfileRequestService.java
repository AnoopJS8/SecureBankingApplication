package com.bankapp.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.User;
import com.bankapp.repositories.ProfileRequestRepository;

@Service
public class ProfileRequestService implements IProfileRequestService, Constants {

    @Autowired
    private ProfileRequestRepository profileRequestRepository;

    @Transactional
    @Override
    public String saveProfileRequest(ProfileRequest profile) {
        try {
            profileRequestRepository.save(profile);
            return SUCCESS;
        } catch (Exception e) {
            return ERROR;
        }
    }

    @Override
    public List<ProfileRequest> getPendingRequests() {
        Long manager = new Long(1);
        Long employee = new Long(6);
        List<ProfileRequest> profileRequest = profileRequestRepository.findByStatusAndRoleId(S_PROFILE_UPDATE_PENDING,
                manager);
        profileRequest.addAll(profileRequestRepository.findByStatusAndRoleId(S_PROFILE_UPDATE_PENDING, employee));
        return profileRequest;
    }

    @Transactional
    @Override
    public void setRequestToVerified(Long id) {
        ProfileRequest profile = profileRequestRepository.findOne(id);
        changeUserData(profile);
        profile.setStatus(S_PROFILE_UPDATE_VERFIED);
        profileRequestRepository.save(profile);
    }

    private void changeUserData(ProfileRequest profile) {
        User user = profile.getUser();
        user.setAddress(profile.getAddress());
        user.setPhoneNumber(profile.getPhoneNumber());
        user.setDateOfBirth(profile.getDateOfBirth());
    }

    @Override
    public void declineRequest(Long id) {
        ProfileRequest profile = profileRequestRepository.findOne(id);
        profile.setStatus(S_PROFILE_UPDATE_DECLINED);
        profileRequestRepository.save(profile);
        
    }

}
