package com.bankapp.services;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.User;

public interface IUserService {
    User registerNewUserAccount(User accountDto)     
            throws EmailExistsException;
}
