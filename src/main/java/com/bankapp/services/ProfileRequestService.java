package com.bankapp.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.repositories.ProfileRequestRepository;
import com.bankapp.repositories.RoleRepository;

@Service
public class ProfileRequestService implements IProfileRequestService, Constants {

    @Autowired
    private RoleRepository roleRepository;

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
        Role manager = roleRepository.findByName("ROLE_MANAGER");
        Role employee = roleRepository.findByName("ROLE_EMPLOYEE");
        List<ProfileRequest> profileRequest = profileRequestRepository.findByStatusAndRole(S_PROFILE_UPDATE_PENDING,
                manager);
        profileRequest.addAll(profileRequestRepository.findByStatusAndRole(S_PROFILE_UPDATE_PENDING, employee));
        return profileRequest;
    }

    @Transactional
    @Override
    public void setRequestToVerified(String id) {
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
    public void declineRequest(String id) {
        ProfileRequest profile = profileRequestRepository.findOne(id);
        profile.setStatus(S_PROFILE_UPDATE_DECLINED);
        profileRequestRepository.save(profile);

    }

    @Override
    public List<ProfileRequest> getRequestsByStatus(String status) {
        Role merchant = roleRepository.findByName("ROLE_MERCHANT");
        Role customer = roleRepository.findByName("ROLE_CUSTOMER");

        List<ProfileRequest> list = profileRequestRepository.findByStatusAndRole(status, merchant);

        list.addAll(profileRequestRepository.findByStatusAndRole(status, customer));
        return list;
    }

    @Override
    public ProfileRequest getRequestById(String id) {

        ProfileRequest request = profileRequestRepository.findOne(id);
        return request;
    }

    public String authorizeRequest(ProfileRequest requests) {

        String result = "";

        requests.setStatus(S_PROFILE_UPDATE_VERFIED);

        try {
            profileRequestRepository.save(requests);
            result = "Profile modification has been approved";

        } catch (Exception e) {
            result = "unsuccessull";
        }

        return result;
    }

    @Override
    public String declineRequest(ProfileRequest requests) {
        // TODO Auto-generated method stub
        String result = "";
        requests.setStatus(S_PROFILE_UPDATE_DECLINED);

        try {
            profileRequestRepository.save(requests);
            result = "Profile modification has been declined";

        } catch (Exception e) {
            result = "unsuccessull";
        }

        return result;
    }

}
