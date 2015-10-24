package com.bankapp.services;


import java.util.List;

import com.bankapp.models.ProfileRequest;

public interface IProfileRequestService {

    public String saveProfileRequest(ProfileRequest profile);

    List<ProfileRequest> getPendingRequests();

    void declineRequest(String id);

    public List<ProfileRequest> getRequestsByStatus(String status);

    public ProfileRequest getRequestById(String Id);

    public String authorizeRequest(ProfileRequest requests);

    public String declineRequest(ProfileRequest requests);
}
