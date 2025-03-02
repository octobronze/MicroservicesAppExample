package com.example.user_service.dtos;

import java.util.Map;

public class EmailSendDto {
    private String methodName;
    private String email;
    private Map<String, Object> params;

    public EmailSendDto() {

    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
