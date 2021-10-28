package com.project.timesheet.service.sheet;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.sheets.v4.model.*;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.Sheets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class SheetsIntegrationImpl implements SheetsIntegration {

    @Override
    public void updateSheetValue(String spreadSheetId, String sheetId, String valueId, String value, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList(value)));

            UpdateValuesResponse result = sheetsService.spreadsheets().values().update(spreadSheetId, valueId, body).setValueInputOption("RAW").execute();
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }

    @Override
    public List<String> getSheetTitles(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            List<String> sheetTitles = new ArrayList<>();

            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            Spreadsheet spreadsheetResponse = sheetsService.spreadsheets().get(spreadSheetId).execute();

            spreadsheetResponse.getSheets().forEach(
                    sheet -> {
                        sheetTitles.add(sheet.getProperties().getTitle());
                    });

            return sheetTitles;
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }

    @Override
    public boolean doesSheetExist(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            sheetsService.spreadsheets().values().batchGet(spreadSheetId).setRanges(Arrays.asList("'" + sheetId + "'")).execute();

            sheetsService.spreadsheets().get(spreadSheetId).execute();

            return true;
        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public boolean isAuthorized(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            sheetsService.spreadsheets().get(spreadSheetId).execute();

            return true;
        } catch (Exception e) {

            return false;
        }
    }
}
