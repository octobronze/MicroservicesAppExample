package com.example.email_service.enums;

import com.example.email_service.exceptions.BadRequestException;

import static com.example.email_service.consts.ExceptionConsts.NO_SUCH_METHOD_IN_MAIL_SERVICE_EXISTS;

public enum MailServiceMethodNamesEnum {
    SEND_VERIFICATION_LINK("sendVerificationLink");

    private final String methodName;

    MailServiceMethodNamesEnum(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public static MailServiceMethodNamesEnum stringToEnum(String methodName) {
        for (var value : MailServiceMethodNamesEnum.values()) {
            if (value.getMethodName().equals(methodName)) return value;
        }

        throw new BadRequestException(NO_SUCH_METHOD_IN_MAIL_SERVICE_EXISTS);
    }
}
