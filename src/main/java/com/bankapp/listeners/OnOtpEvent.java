package com.bankapp.listeners;


import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class OnOtpEvent extends ApplicationEvent{
    private final String resourceName;
    private final Long resourceId;
 
    public OnOtpEvent(Long resourceId, String resourceName) {
        super(resourceName);
         
        this.resourceId = resourceId;
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Long getResourceId() {
        return resourceId;
    }

    
}
