package com.project.timesheet.service.sheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;

public class SheetsServiceUtil {

    private static final String APPLICATION_NAME = "Time Sheets App";

    public static Sheets getSheetsService(TokenResponse tokenResponse, String clientId) throws Exception {
        /* set `Scope` and `TokenType` in `GoogleAuthorizeUtil.authorize` function */

        Credential credential = GoogleAuthorizeUtil.authorize(tokenResponse, clientId);

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }

    public static Sheets getSheetsService() throws Exception {

        Credential credential = GoogleAuthorizeUtil.authorize();

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }

    public static Credential getCredential() throws Exception {

        Credential credential = GoogleAuthorizeUtil.authorize();

        return credential;
    }
}