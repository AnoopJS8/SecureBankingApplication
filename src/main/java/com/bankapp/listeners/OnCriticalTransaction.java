package com.bankapp.listeners;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.bankapp.models.Transaction;

@SuppressWarnings("serial")
public class OnCriticalTransaction extends ApplicationEvent{
    private final String appUrl;
    private final Locale locale;
    private final Transaction transaction;
 
    public OnCriticalTransaction(Transaction transaction, Locale locale, String appUrl) {
        super(transaction);
         
        this.transaction = transaction;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
