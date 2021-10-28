package com.project.timesheet.dto;

import lombok.Data;

@Data
public class LoginDTO {

    private String accessToken;

    private String refreshToken;

    private String expiresInSeconds;
}