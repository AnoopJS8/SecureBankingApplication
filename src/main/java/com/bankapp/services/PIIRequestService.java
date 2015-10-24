package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.PersonalIdentificationInfo;
import com.bankapp.models.PiiRequest;
import com.bankapp.repositories.PIIRepository;
import com.bankapp.repositories.PIIRequestRepository;

@Service
public class PIIRequestService implements IPIIRequestService, Constants {

    @Autowired
    PIIRequestRepository piiRequestRepository;
    
    @Autowired
    PIIRepository piiRepository;

    @Transactional
    @Override
    public String saveRequest(PiiRequest piiRequest) {
        piiRequest.setStatus(S_PII_REQUEST_PENDING);
        try {
            piiRequestRepository.save(piiRequest);
            return SUCCESS;
        } catch (Exception e) {
            return ERROR;
        }
    }

    @Transactional
    @Override
    public List<PiiRequest> getPiiAdminRequest() {
        List<PiiRequest> listPiiRequest = piiRequestRepository.findByStatus(S_PII_REQUEST_PENDING);
        return listPiiRequest;
    }
    
    @Transactional
    @Override
    public String authorize(PiiRequest piiRequest) {
        try{
            PiiRequest request = piiRequestRepository.findOne(piiRequest.getPiiRId());
            PersonalIdentificationInfo pii = piiRepository.findByEmail(request.getEmail()) ;
            pii.setStatus(S_PII_AUTHORIZED); //Saving for the pii information so the admin can see it
            request.setStatus(S_PII_REQUEST_DONE); 
            piiRequestRepository.save(request);
        }catch(Exception e){
            return ERROR;
        }
       
        return SUCCESS;
    }

    @Override
    public String decline(PiiRequest piiRequest) {
        try{
            PiiRequest request = piiRequestRepository.findOne(piiRequest.getPiiRId());
            PersonalIdentificationInfo pii = piiRepository.findByEmail(request.getEmail()) ;
            pii.setStatus(S_PII_DECLINED); //Saving for the pii information so the admin can see it
            request.setStatus(S_PII_REQUEST_DONE); 
            piiRequestRepository.save(request);
        }catch(Exception e){
            return ERROR;
        }
       
        return SUCCESS;
    }
}
