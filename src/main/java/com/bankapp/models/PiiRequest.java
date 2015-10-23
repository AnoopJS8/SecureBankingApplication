package com.bankapp.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NotFound;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "piirequest")
public class PiiRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long piiRId;

    @NotEmpty
    @NotFound
    @Email
    private String email;
    
    @NotNull
    private String status;

    public Long getPiiRId() {
        return piiRId;
    }

    public void setPiiRId(Long piiRId) {
        this.piiRId = piiRId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
