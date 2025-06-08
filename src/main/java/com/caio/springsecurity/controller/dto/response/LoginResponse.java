package com.caio.springsecurity.controller.dto.response;

public record LoginResponse(String accessToken, Long expiresIn) {
}
