
package com.bankapp.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "profilerequest")
public class ProfileRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long rId;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    private String address;

    private String phoneNumber;

    private String dateOfBirth;
    
    private Long roleId;
    
    @NotNull
    private String status;

    public Long getrId() {
        return rId;
    }

    public void setrId(Long rId) {
        this.rId = rId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
    	final String value = String
				.format("Profile [uid=%s, address=%s, phonenumber=%s, dob=%s, status=%s]",
						user.getId(), address, phoneNumber, dateOfBirth, status);
        return value;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long long1) {
        this.roleId = long1;
    }

}

