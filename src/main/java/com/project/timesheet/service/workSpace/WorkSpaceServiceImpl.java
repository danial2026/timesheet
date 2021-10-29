package com.project.timesheet.service.workSpace;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.dto.*;
import com.project.timesheet.entity.*;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.exception.ErrorCode;
import com.project.timesheet.repository.WorkSpaceRepository;
import com.project.timesheet.service.sheet.GoogleAuthorizeUtil;
import com.project.timesheet.service.sheet.SheetsIntegration;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class  WorkSpaceServiceImpl implements WorkSpaceService{

    @Autowired
    private WorkSpaceRepository workSpaceRepository;

    @Autowired
    private SheetsIntegration sheetsIntegration;

    @Override
    public LoginResponseDTO login() throws BusinessServiceException {
        try {
            // ask google auth library to generate a url for authenticating the app
            String url = GoogleAuthorizeUtil.authorize();

            LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
            loginResponseDTO.setUrl(url);

            return loginResponseDTO;
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }

    @Override
    public LoginDTO authenticateCode(String code) throws BusinessServiceException {
        try {
            // ask google auth library to authenticate Code and generate access and refresh token
            TokenResponse tokenResponse = GoogleAuthorizeUtil.authenticateCode(code);

            return convertTokenResponseToLoginDTO(tokenResponse);
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
    }

    @Override
    public String createDefaultSheet(String workSpaceId, String sheetTitle, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        Optional<WorkSpaceEntity> optionalWorkSpaceEntity = workSpaceRepository.findById(workSpaceId);

        if (optionalWorkSpaceEntity.isEmpty()) {

            throw new BusinessServiceException(ErrorCode.NOT_FOUND);
        }

        WorkSpaceEntity workSpaceEntity = optionalWorkSpaceEntity.get();

        // check if token does have acces by getting sheets list or sth
        if (!sheetsIntegration.isAuthorized(workSpaceEntity.getSpreadSheetId(), tokenResponse, clientId)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
        // check if sheet does exist
        if (!sheetsIntegration.doesSpreedSheetExist(workSpaceEntity.getSpreadSheetId(), tokenResponse, clientId)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        LocalDateTime dateNow = LocalDateTime.now();
        int monthNumber = dateNow.getMonthValue() - 1;

        sheetTitle = getMonthFromInt(monthNumber);

        String sheetId = sheetsIntegration.createSheet(workSpaceEntity.getSpreadSheetId(), sheetTitle, tokenResponse, clientId);

        // set weekday name and date for chosen month
        sheetsIntegration.addMonthDays(workSpaceEntity.getSpreadSheetId(), sheetTitle, monthNumber, tokenResponse, clientId);
        // set all cell text horizontal alignment as centered
        sheetsIntegration.centerAllCellTexts(workSpaceEntity.getSpreadSheetId(), sheetId, tokenResponse, clientId);
        // change header row(first row) text color and background color
        sheetsIntegration.setHeadersRowColor(workSpaceEntity.getSpreadSheetId(), sheetId, tokenResponse, clientId);

        return sheetId;
    }

    @Override
    public void createWorkSpace(CreateWorkSpaceDTO createWorkSpaceDTO, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        // first check if token does have acces by getting sheets list or sth
        if (!sheetsIntegration.isAuthorized(createWorkSpaceDTO.getSpreadSheetId(), tokenResponse, clientId)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        WorkSpaceEntity newWorkSpaceEntity = new WorkSpaceEntity();
        newWorkSpaceEntity.setWorkSpaceTitle(createWorkSpaceDTO.getWorkSpaceTitle());
        newWorkSpaceEntity.setSpreadSheetId(createWorkSpaceDTO.getSpreadSheetId());
        newWorkSpaceEntity.setEmail(createWorkSpaceDTO.getEmail());

        workSpaceRepository.save(newWorkSpaceEntity);
    }

    @Override
    public void updateWorkSpace(UpdateWorkSpaceDTO updateWorkSpaceDTO, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {

        // first check if token does have acces by getting sheets list or sth
        if (!sheetsIntegration.isAuthorized(updateWorkSpaceDTO.getSpreadSheetId(), tokenResponse, clientId)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
        Optional<WorkSpaceEntity> optionalWorkSpaceEntity = workSpaceRepository.findById(updateWorkSpaceDTO.getWorkSpaceId());
        if (optionalWorkSpaceEntity.isEmpty()){

            throw new BusinessServiceException(ErrorCode.INVALID_VALUE);
        }

        WorkSpaceEntity newWorkSpaceEntity = optionalWorkSpaceEntity.get();

        if (!updateWorkSpaceDTO.getWorkSpaceTitle().isEmpty()) {
            newWorkSpaceEntity.setWorkSpaceTitle(updateWorkSpaceDTO.getWorkSpaceTitle());
        }
        if (!updateWorkSpaceDTO.getSpreadSheetId().isEmpty()) {
            newWorkSpaceEntity.setSpreadSheetId(updateWorkSpaceDTO.getSpreadSheetId());
        }
        if (!updateWorkSpaceDTO.getEmail().isEmpty()) {
            newWorkSpaceEntity.setEmail(updateWorkSpaceDTO.getEmail());
        }

        workSpaceRepository.save(newWorkSpaceEntity);
    }

    @Override
    public void finishWorking(FinishWorkingRequestDTO finishWorkingRequestDTO, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        Optional<WorkSpaceEntity> optionalWorkSpaceEntity = workSpaceRepository.findById(finishWorkingRequestDTO.getWorkSpaceId());
        if (optionalWorkSpaceEntity.isEmpty()){

            throw new BusinessServiceException(ErrorCode.INVALID_VALUE);
        }
        WorkSpaceEntity newWorkSpaceEntity = optionalWorkSpaceEntity.get();

        if (!sheetsIntegration.isAuthorized(newWorkSpaceEntity.getSpreadSheetId(), tokenResponse, clientId)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        LocalDateTime dateNow = LocalDateTime.now();
        int dayOfMonth = dateNow.getDayOfMonth();

        sheetsIntegration.addFinishedTask(newWorkSpaceEntity.getSpreadSheetId(), finishWorkingRequestDTO.getSheetId(), dayOfMonth,finishWorkingRequestDTO.getTaskId(), tokenResponse, clientId);
    }

    @Override
    public List<WorkSpaceDTO> getAllWorkSpaces(String email) throws BusinessServiceException {
        List<WorkSpaceDTO> listWorkSpaceDTOs = new ArrayList<>();
        List<WorkSpaceEntity> listWorkSpaceEntity = workSpaceRepository.findAllByEmail(email);

        listWorkSpaceEntity.forEach(
                item -> {
                            listWorkSpaceDTOs.add(convertEntityToWorkSpaceDTO(item));
                        }
                );

        return listWorkSpaceDTOs;
    }

    @Override
    public WorkSpaceDetailDTO getWorkSpaceDetail(String workSpaceId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException {
        Optional<WorkSpaceEntity> optionalWorkSpaceEntity = workSpaceRepository.findById(workSpaceId);

        if (optionalWorkSpaceEntity.isEmpty()) {

            throw new BusinessServiceException(ErrorCode.NOT_FOUND);
        }

        WorkSpaceEntity workSpaceEntity = optionalWorkSpaceEntity.get();

        // check if token does have acces by getting sheets list or sth
        if (!sheetsIntegration.isAuthorized(workSpaceEntity.getSpreadSheetId(), tokenResponse, clientId)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
        // check if sheet does exist
        if (!sheetsIntegration.doesSpreedSheetExist(workSpaceEntity.getSpreadSheetId(), tokenResponse, clientId)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
        List<SheetDTO> sheets = sheetsIntegration.getSheets(workSpaceEntity.getSpreadSheetId(), tokenResponse, clientId);

        return convertEntityToWorkSpaceDetailDTO(workSpaceEntity, sheets);
    }

    private WorkSpaceDetailDTO convertEntityToWorkSpaceDetailDTO(WorkSpaceEntity workSpaceEntity, List<SheetDTO> sheets){
        WorkSpaceDetailDTO workSpaceDetailDTO =  new WorkSpaceDetailDTO();
        workSpaceDetailDTO.setWorkSpaceId(workSpaceEntity.getId());
        workSpaceDetailDTO.setWorkSpaceTitle(workSpaceEntity.getWorkSpaceTitle());
        workSpaceDetailDTO.setSpreadSheetId(workSpaceEntity.getSpreadSheetId());
        workSpaceDetailDTO.setSheets(sheets);

        return workSpaceDetailDTO;
    }

    private WorkSpaceDTO convertEntityToWorkSpaceDTO(WorkSpaceEntity workSpaceEntity){
        WorkSpaceDTO workSpaceDTO =  new WorkSpaceDTO();
        workSpaceDTO.setWorkSpaceId(workSpaceEntity.getId());
        workSpaceDTO.setWorkSpaceTitle(workSpaceEntity.getWorkSpaceTitle());

        return workSpaceDTO;
    }

    private LoginDTO convertTokenResponseToLoginDTO(TokenResponse tokenResponse) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setAccessToken(tokenResponse.getAccessToken());
        loginDTO.setRefreshToken(tokenResponse.getRefreshToken());
        loginDTO.setExpiresInSeconds(tokenResponse.getExpiresInSeconds().toString());

        return loginDTO;
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
}
