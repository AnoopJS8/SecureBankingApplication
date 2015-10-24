package com.bankapp.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import com.bankapp.constants.Constants;


@Controller
@Secured("ROLE_CUSTOMER")
public class CustomerController implements Constants {

}