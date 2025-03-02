package com.example.gateway_service.auth.other;

import io.jsonwebtoken.Claims;

public record JwtCheckResponse(boolean isValid, String responseString, Claims claims) {
}
