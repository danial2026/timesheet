package com.project.timesheet.service.sheet;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.sheets.v4.model.*;
import com.project.timesheet.dto.SheetDTO;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.Sheets;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class SheetsIntegrationImpl implements SheetsIntegration {

    @Override
    public void updateSheetValue(String spreadSheetId, String sheetId, String valueId, String value, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);
            valueId = "'" + getSheetTitle(spreadSheetId, sheetId, tokenResponse, clientId) + "'!" + valueId;

            ValueRange getResult = sheetsService.spreadsheets().values().get(spreadSheetId, valueId).execute();

            String currentValue = getResult.getValues().get(0).get(0).toString();

            ValueRange body = new ValueRange().setValues(Arrays.asList(Arrays.asList(currentValue + "\n" + value)));

            // read this if you need to know what does `USER_ENTERED` or `RAW` means
            // https://stackoverflow.com/questions/37785216/google-sheets-api-v4-and-valueinputoption
            UpdateValuesResponse result = sheetsService.spreadsheets().values().update(spreadSheetId, valueId, body).setValueInputOption("RAW").execute();
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.INVALID_VALUE);
        }
    }

    @Override
    public String getSheetTitle(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            Spreadsheet spreadsheetResponse = sheetsService.spreadsheets().get(spreadSheetId).execute();

            for (Sheet sheet: spreadsheetResponse.getSheets()) {
                if (sheet.getProperties().getSheetId().toString().equals(sheetId)){
                    return sheet.getProperties().getTitle();
                }
            }
            throw new BusinessServiceException(ErrorCode.NOT_FOUND);
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_FOUND);
        }
    }

    @Override
    public List<SheetDTO> getSheets(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            List<SheetDTO> sheets = new ArrayList<>();

            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            Spreadsheet spreadsheetResponse = sheetsService.spreadsheets().get(spreadSheetId).execute();

            spreadsheetResponse.getSheets().forEach(
                    sheet -> {
                        SheetDTO sheetDTO = new SheetDTO();
                        sheetDTO.setSheetTitle(sheet.getProperties().getTitle());
                        sheetDTO.setSheetId(sheet.getProperties().getSheetId());
                        sheets.add(sheetDTO);
                    });

            return sheets;
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }

    @Override
    public void addFinishedTask(String spreadSheetId, String sheetId, int dayOfMonth, String value, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            String valueId = "C" + String.valueOf(dayOfMonth + 1);

            updateSheetValue(spreadSheetId, sheetId, valueId, value, tokenResponse, clientId);
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
    public boolean isAuthorized(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            sheetsService.spreadsheets().get(spreadSheetId).execute();

            return true;
        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public boolean doesSpreedSheetExist(String spreadSheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            GridRange gridRange = new GridRange();

            GetSpreadsheetByDataFilterRequest getSpreadsheetByDataFilterRequest = new GetSpreadsheetByDataFilterRequest();
            DataFilter dataFilter= new DataFilter();
            dataFilter.setGridRange(gridRange);

            List<DataFilter> filters = new ArrayList<>();
            filters.add(dataFilter);

            getSpreadsheetByDataFilterRequest.setDataFilters(filters);
            Spreadsheet batchResponse = sheetsService.spreadsheets().getByDataFilter(spreadSheetId, getSpreadsheetByDataFilterRequest).execute();

            return true;
        } catch (Exception e) {

            return false;
        }
    }
    @Override
    public boolean doesSheetExist(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            GridRange gridRange = new GridRange();
            gridRange.setSheetId(Integer.parseInt(sheetId));

            GetSpreadsheetByDataFilterRequest getSpreadsheetByDataFilterRequest = new GetSpreadsheetByDataFilterRequest();
            DataFilter dataFilter= new DataFilter();
            dataFilter.setGridRange(gridRange);

            List<DataFilter> filters = new ArrayList<>();
            filters.add(dataFilter);

            getSpreadsheetByDataFilterRequest.setDataFilters(filters);
            Spreadsheet batchResponse = sheetsService.spreadsheets().getByDataFilter(spreadSheetId, getSpreadsheetByDataFilterRequest).execute();

            return true;
        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public String createSheet(String spreadSheetId, String sheetTitle, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            // create an empty new sheet
            List<Request> requests = new ArrayList<>();

            requests.add(new Request().setAddSheet(new AddSheetRequest()
                    .setProperties(new SheetProperties()
                            .setTitle(sheetTitle))));

            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(spreadSheetId, body).execute();

            return response.getReplies().get(0).getAddSheet().getProperties().getSheetId().toString();
        } catch (Exception e) {
            if (e.getMessage().contains("already exists")){

                throw new BusinessServiceException(ErrorCode.ALREADY_EXIST);
            }

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }

    @Override
    public void addMonthDays(String spreadSheetId, String sheetTitle, int monthNumber, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            List<List<Object>> writeData = new ArrayList<>();
            writeData.addAll(getMonthDays(monthNumber));

            ValueRange oRange = new ValueRange();
            oRange.setRange("'" + sheetTitle + "'!A1"); //
            oRange.setValues(writeData);

            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);

            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);

            BatchUpdateValuesResponse oResp1 = sheetsService.spreadsheets().values().batchUpdate(spreadSheetId, oRequest).execute();
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }

    @Override
    public void setHeadersRowColor(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            TextFormat textFormat = new TextFormat();
            Color testColor = new Color().setRed(1F).setGreen(1F).setBlue(1F);
            textFormat.setForegroundColor(testColor);
            textFormat.setFontSize(10);

            Color color = new Color().setRed(0.3F).setGreen(0.6F).setBlue(0.9F);

            CellFormat cellFormat = new CellFormat();
            cellFormat.setBackgroundColor(color);
            cellFormat.setTextFormat(textFormat);
            cellFormat.setHorizontalAlignment("CENTER");

            CellData cellData = new CellData();
            cellData.setUserEnteredFormat(cellFormat);

            GridRange gridRange = new GridRange();
            gridRange.setSheetId(Integer.parseInt(sheetId));
            gridRange.setStartRowIndex(0);
            gridRange.setEndRowIndex(1);
            gridRange.setStartColumnIndex(0);
            gridRange.setEndColumnIndex(8);

            List<Request> requestList = new ArrayList<>();

            requestList.add(new Request().setRepeatCell(new RepeatCellRequest().setCell(cellData).setRange(gridRange).setFields("userEnteredFormat")));

            BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
            batchUpdateSpreadsheetRequest.setRequests(requestList);

            BatchUpdateSpreadsheetResponse batchUpdateResponse = sheetsService.spreadsheets().batchUpdate(spreadSheetId, batchUpdateSpreadsheetRequest).execute();
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }

    @Override
    public void centerAllCellTexts(String spreadSheetId, String sheetId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        try {
            Sheets sheetsService = SheetsServiceUtil.getSheetsService(tokenResponse, clientId);

            CellFormat cellFormat = new CellFormat();
            cellFormat.setHorizontalAlignment("CENTER");

            CellData cellData = new CellData();
            cellData.setUserEnteredFormat(cellFormat);

            GridRange gridRange = new GridRange();
            gridRange.setSheetId(Integer.parseInt(sheetId));

            List<Request> requestList = new ArrayList<>();

            requestList.add(new Request().setRepeatCell(new RepeatCellRequest().setCell(cellData).setRange(gridRange).setFields("userEnteredFormat")));

            BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
            batchUpdateSpreadsheetRequest.setRequests(requestList);

            BatchUpdateSpreadsheetResponse batchUpdateResponse = sheetsService.spreadsheets().batchUpdate(spreadSheetId, batchUpdateSpreadsheetRequest).execute();
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }


    private List<List<Object>> getMonthDays(int startMonth, int startDay, int endMonth, int endDay) throws BusinessServiceException{
        if (startMonth > endMonth){

            throw new BusinessServiceException(ErrorCode.INVALID_VALUE);
        }else if(startMonth == endMonth){
            if (startDay >= endDay){

                throw new BusinessServiceException(ErrorCode.INVALID_VALUE);
            }
        }
        List<List<Object>> dataColumn = new ArrayList<>();

        for (int monthNumber = 0 ; monthNumber < endMonth-startMonth ; monthNumber++ ){


            Calendar cal = Calendar.getInstance();
            if (monthNumber == 0){
                cal.set(Calendar.DAY_OF_MONTH, startMonth);
            }else if(monthNumber == endMonth-startMonth -1){
                cal.set(Calendar.DAY_OF_MONTH, startMonth);
            }
            cal.set(Calendar.MONTH, startDay);
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);


            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            List<Object> headersRow = new ArrayList<>();
            headersRow.add("Day");
            headersRow.add("Date");
            headersRow.add("Task");
            headersRow.add("Actual");
            headersRow.add("Goal");
            headersRow.add("Personal Hours");
            headersRow.add("Start working");
            headersRow.add("Finish working");
            dataColumn.add(headersRow);
            for (int i = 1; i <= maxDay; i++) {
                cal.set(Calendar.DAY_OF_MONTH, i);
                List<Object> dataRow = new ArrayList<>();

                dataRow.add(weekDayInDanish(cal.get(Calendar.DAY_OF_WEEK)));
                dataRow.add(getMonthFromInt(monthNumber) + " " + i);

                dataColumn.add(dataRow);
            }
        }
        return dataColumn;

    }

    private List<List<Object>> getMonthDays(int month){
        List<List<Object>> dataColumn = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        List<Object> headersRow = new ArrayList<>();
        headersRow.add("Day");
        headersRow.add("Date");
        headersRow.add("Task");
        headersRow.add("Actual");
        headersRow.add("Goal");
        headersRow.add("Personal Hours");
        headersRow.add("Start working");
        headersRow.add("Finish working");
        dataColumn.add(headersRow);
        for (int i = 1; i <= maxDay; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            List<Object> dataRow = new ArrayList<>();

            dataRow.add(weekDayInDanish(cal.get(Calendar.DAY_OF_WEEK)));
            dataRow.add(getMonthFromInt(month) + " " + i);

            dataColumn.add(dataRow);
        }
        return dataColumn;
    }

    private String getMonthFromInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    private String weekDayInDanish(int dow) {
        switch (dow) {
            case Calendar.SUNDAY: return "Sunday";
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            default: throw new IllegalArgumentException("Unexpected day: " + dow);
        }
    }
}
