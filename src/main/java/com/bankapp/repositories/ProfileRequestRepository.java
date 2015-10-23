package com.bankapp.repositories;

import java.util.Collection;
import java.util.List;






import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Transaction;

public interface ProfileRequestRepository extends CrudRepository<ProfileRequest, Long> {

	
	List<ProfileRequest> findByStatus(String status);
	
	
	ProfileRequest findByRId(Long Id);


	List<ProfileRequest> findByStatusAndRoleId(String status, Long merchant);



}
