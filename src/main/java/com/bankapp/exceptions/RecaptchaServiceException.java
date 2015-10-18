package com.bankapp.exceptions;

import org.springframework.web.client.RestClientException;

public class RecaptchaServiceException extends Exception {

    public RecaptchaServiceException(String string, RestClientException e) {
        // TODO Auto-generated constructor stub
    }

}
