package com.project.timesheet.service.sheet;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.exception.BusinessServiceException;

import java.util.List;

public interface SheetsIntegration {

    boolean doesSheetExist(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    boolean isAuthorized(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void updateSheetValue(String spreadSheetId, String sheetId, String valueId, String value, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    List<String> getSheetTitles(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;
}
