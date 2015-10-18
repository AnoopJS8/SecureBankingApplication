package com.bankapp.repositories;

import java.util.List;




import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Requests;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface RequestRepository extends CrudRepository<Requests, Long>{

	 List<Requests> findById(Long id);
	
}