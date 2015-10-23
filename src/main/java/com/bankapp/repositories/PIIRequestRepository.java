package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.PiiRequest;

public interface PIIRequestRepository extends CrudRepository<PiiRequest, Long>{
    public List<PiiRequest> findByStatus(String status);
}
