package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.ProfileRequest;

public interface ProfileRequestRepository extends CrudRepository<ProfileRequest, Long> {
    
	List<ProfileRequest> findByStatus(String status);
	
	ProfileRequest findByRId(Long id);
	
}
