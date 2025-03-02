package com.example.user_service.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VerificationCodeService {
    private final static Map<String, String> codesByEmailMap = new HashMap<>();

    public String generateCodeForUser(String email) {
        String code = String.valueOf((int) (Math.random() * 1000));

        codesByEmailMap.put(email, code);

        return code;
    }

    public boolean checkCodeForUser(String email, String code) {
        if (codesByEmailMap.containsKey(email) && codesByEmailMap.get(email).equals(code)) {
            codesByEmailMap.remove(email);

            return true;
        }

        return false;
    }
}
