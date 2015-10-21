package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.ProfileRequest;

public interface ProfileRequestRepository extends CrudRepository<ProfileRequest, Long> {
    
}
