package com.example.email_service.dtos;

import java.util.Map;

public class EmailReceiveDto {
    private String methodName;
    private String email;
    private Map<String, Object> params;

    public EmailReceiveDto() {

    }

    public EmailReceiveDto(String methodName, String email, Map<String, Object> params) {
        this.methodName = methodName;
        this.email = email;
        this.params = params;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
