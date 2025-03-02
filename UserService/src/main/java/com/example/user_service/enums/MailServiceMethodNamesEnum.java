package com.example.user_service.enums;

public enum MailServiceMethodNamesEnum {
    SEND_VERIFICATION_LINK("sendVerificationLink");

    private final String methodName;

    MailServiceMethodNamesEnum(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}
