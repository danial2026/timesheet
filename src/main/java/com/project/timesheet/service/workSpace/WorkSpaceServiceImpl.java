package com.project.timesheet.service.workSpace;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.dto.*;
import com.project.timesheet.entity.WorkSpaceEntity;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.exception.ErrorCode;
import com.project.timesheet.repository.WorkSpaceRepository;
import com.project.timesheet.service.jira.JiraClientImpl;
import com.project.timesheet.service.sheet.SheetsIntegration;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private WorkSpaceRepository workSpaceRepository;

    @Autowired
    private SheetsIntegration sheetsIntegration;

    @Override
    public UserDTO getUserInfo(String token) throws BusinessServiceException {
        JSONObject issueJson = null;
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://www.googleapis.com/oauth2/v1/userinfo?alt=json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .asJson();

            if (response.getStatus() != 200) {

                throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
            }
            issueJson = response.getBody().getObject();
        } catch (Exception ignored) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(issueJson.getString("id"));
        userDTO.setEmail(issueJson.getString("email"));
        userDTO.setVerified_email(issueJson.getString("verified_email"));
        userDTO.setName(issueJson.getString("name"));
        userDTO.setGiven_name(issueJson.getString("given_name"));
        userDTO.setFamily_name(issueJson.getString("family_name"));
        userDTO.setPicture(issueJson.getString("picture"));
        userDTO.setFamily_name(issueJson.getString("family_name"));
        userDTO.setLocale(issueJson.getString("locale"));

        return userDTO;
    }

    @Override
    public String createDefaultSheet(String workSpaceId, String sheetTitle, TokenResponse tokenResponse) throws BusinessServiceException {
        Optional<WorkSpaceEntity> optionalWorkSpaceEntity = workSpaceRepository.findById(workSpaceId);

        if (optionalWorkSpaceEntity.isEmpty()) {

            throw new BusinessServiceException(ErrorCode.NOT_FOUND);
        }

        WorkSpaceEntity workSpaceEntity = optionalWorkSpaceEntity.get();

        // check if token does have acces by getting sheets list or sth
        if (!sheetsIntegration.isAuthorized(workSpaceEntity.getSpreadSheetId(), tokenResponse)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
        // check if sheet does exist
        if (!sheetsIntegration.doesSpreedSheetExist(workSpaceEntity.getSpreadSheetId(), tokenResponse)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        LocalDateTime dateNow = LocalDateTime.now();
        int monthNumber = dateNow.getMonthValue() - 1;

        sheetTitle = getMonthFromInt(monthNumber);

        String sheetId = sheetsIntegration.createSheet(workSpaceEntity.getSpreadSheetId(), sheetTitle, tokenResponse);

        // set weekday name and date for chosen month
        sheetsIntegration.addMonthDays(workSpaceEntity.getSpreadSheetId(), sheetTitle, monthNumber, tokenResponse);
        // set all cell text horizontal alignment as centered
        sheetsIntegration.centerAllCellTexts(workSpaceEntity.getSpreadSheetId(), sheetId, tokenResponse);
        // change header row(first row) text color and background color
        sheetsIntegration.setHeadersRowColor(workSpaceEntity.getSpreadSheetId(), sheetId, tokenResponse);

        return sheetId;
    }

    @Override
    public void createWorkSpace(CreateWorkSpaceDTO createWorkSpaceDTO, TokenResponse tokenResponse) throws BusinessServiceException {
        // check token
        UserDTO userDTO = getUserInfo(tokenResponse.getAccessToken());
        String email = userDTO.getEmail();
        // first check if token does have acces by getting sheets list or sth
        if (!sheetsIntegration.isAuthorized(createWorkSpaceDTO.getSpreadSheetId(), tokenResponse)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        WorkSpaceEntity newWorkSpaceEntity = new WorkSpaceEntity();
        newWorkSpaceEntity.setWorkSpaceTitle(createWorkSpaceDTO.getWorkSpaceTitle());
        newWorkSpaceEntity.setSpreadSheetId(createWorkSpaceDTO.getSpreadSheetId());
        newWorkSpaceEntity.setEmail(email);

        workSpaceRepository.save(newWorkSpaceEntity);
    }

    @Override
    public void updateWorkSpace(UpdateWorkSpaceDTO updateWorkSpaceDTO, TokenResponse tokenResponse) throws BusinessServiceException {

        // first check if token does have acces by getting sheets list or sth
        if (!sheetsIntegration.isAuthorized(updateWorkSpaceDTO.getSpreadSheetId(), tokenResponse)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
        Optional<WorkSpaceEntity> optionalWorkSpaceEntity = workSpaceRepository.findById(updateWorkSpaceDTO.getWorkSpaceId());
        if (optionalWorkSpaceEntity.isEmpty()) {

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
    public void finishWorking(FinishWorkingRequestDTO finishWorkingRequestDTO, TokenResponse tokenResponse) throws BusinessServiceException {
        Optional<WorkSpaceEntity> optionalWorkSpaceEntity = workSpaceRepository.findById(finishWorkingRequestDTO.getWorkSpaceId());
        if (optionalWorkSpaceEntity.isEmpty()) {

            throw new BusinessServiceException(ErrorCode.INVALID_VALUE);
        }
        WorkSpaceEntity newWorkSpaceEntity = optionalWorkSpaceEntity.get();

        if (!sheetsIntegration.isAuthorized(newWorkSpaceEntity.getSpreadSheetId(), tokenResponse)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        LocalDateTime dateNow = LocalDateTime.now();
        int dayOfMonth = dateNow.getDayOfMonth();

        sheetsIntegration.addFinishedTask(newWorkSpaceEntity.getSpreadSheetId(), finishWorkingRequestDTO.getSheetId(), dayOfMonth, finishWorkingRequestDTO.getTaskId(), tokenResponse);
    }

    @Override
    public List<WorkSpaceDTO> getAllWorkSpaces(TokenResponse tokenResponse) throws BusinessServiceException {
        // check token
        UserDTO userDTO = getUserInfo(tokenResponse.getAccessToken());
        String email = userDTO.getEmail();

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
    public WorkSpaceDetailDTO getWorkSpaceDetail(String workSpaceId, TokenResponse tokenResponse) throws BusinessServiceException {
        Optional<WorkSpaceEntity> optionalWorkSpaceEntity = workSpaceRepository.findById(workSpaceId);

        if (optionalWorkSpaceEntity.isEmpty()) {

            throw new BusinessServiceException(ErrorCode.NOT_FOUND);
        }

        WorkSpaceEntity workSpaceEntity = optionalWorkSpaceEntity.get();

        // check if token does have acces by getting sheets list or sth
        if (!sheetsIntegration.isAuthorized(workSpaceEntity.getSpreadSheetId(), tokenResponse)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
        // check if sheet does exist
        if (!sheetsIntegration.doesSpreedSheetExist(workSpaceEntity.getSpreadSheetId(), tokenResponse)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
        List<SheetDTO> sheets = sheetsIntegration.getSheets(workSpaceEntity.getSpreadSheetId(), tokenResponse);

        return convertEntityToWorkSpaceDetailDTO(workSpaceEntity, sheets);
    }

    @Override
    public JiraIssue getJiraIssue(String username, String password, String jiraUrl, String issueKey) throws BusinessServiceException {
        JiraClientImpl client = new JiraClientImpl(username, password, jiraUrl);

        return client.getIssue(issueKey);
    }

    private WorkSpaceDetailDTO convertEntityToWorkSpaceDetailDTO(WorkSpaceEntity workSpaceEntity, List<SheetDTO> sheets) {
        WorkSpaceDetailDTO workSpaceDetailDTO = new WorkSpaceDetailDTO();
        workSpaceDetailDTO.setWorkSpaceId(workSpaceEntity.getId());
        workSpaceDetailDTO.setWorkSpaceTitle(workSpaceEntity.getWorkSpaceTitle());
        workSpaceDetailDTO.setSpreadSheetId(workSpaceEntity.getSpreadSheetId());
        workSpaceDetailDTO.setSheets(sheets);

        return workSpaceDetailDTO;
    }

    private WorkSpaceDTO convertEntityToWorkSpaceDTO(WorkSpaceEntity workSpaceEntity) {
        WorkSpaceDTO workSpaceDTO = new WorkSpaceDTO();
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
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }
}
