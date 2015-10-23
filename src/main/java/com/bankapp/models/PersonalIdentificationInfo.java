package com.bankapp.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "pii")
public class PersonalIdentificationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long piiId;

    @NotNull
    @NotEmpty
    String email;

    @NotNull
    @NotEmpty
    String pii;

    @NotNull
    String status;

    public Long getPiiId() {
        return piiId;
    }

    public void setPiiId(Long piiId) {
        this.piiId = piiId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPii() {
        return pii;
    }

    public void setPii(String pii) {
        this.pii = pii;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
