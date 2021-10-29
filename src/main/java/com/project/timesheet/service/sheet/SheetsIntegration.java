package com.project.timesheet.service.sheet;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.dto.SheetDTO;
import com.project.timesheet.exception.BusinessServiceException;

import java.util.List;

public interface SheetsIntegration {

    boolean doesSpreedSheetExist(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    boolean doesSheetExist(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void addMonthDays(String spreadSheetId, String sheetTitle,int monthNumber, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void setHeadersRowColor(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void centerAllCellTexts(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    String createSheet(String spreadSheetId, String sheetTitle, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    boolean isAuthorized(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void updateSheetValue(String spreadSheetId, String sheetId, String valueId, String value, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    String getSheetTitle(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    List<SheetDTO> getSheets(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void addFinishedTask(String spreadSheetId, String sheetId, int dayOfMonth, String value, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    List<String> getSheetTitles(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;
}
