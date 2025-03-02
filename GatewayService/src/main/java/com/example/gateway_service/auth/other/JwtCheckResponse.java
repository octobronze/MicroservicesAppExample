package com.example.gateway_service.auth.other;

public class JwtCheckResponse {
    private final boolean isValid;
    private final String responseString;

    public JwtCheckResponse(boolean isValid, String responseString) {
        this.isValid = isValid;
        this.responseString = responseString;
    }

    public String getResponseString() {
        return responseString;
    }

    public boolean isValid() {
        return isValid;
    }
}
