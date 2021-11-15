package com.project.timesheet.service.workSpace;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.dto.*;
import com.project.timesheet.exception.BusinessServiceException;

import java.util.List;

public interface WorkSpaceService {

    UserDTO getUserInfo(String token) throws BusinessServiceException;

    String createDefaultSheet(String spreadSheetId, String sheetTitle, TokenResponse tokenResponse) throws BusinessServiceException;

    void createWorkSpace(CreateWorkSpaceDTO createWorkSpaceDTO, TokenResponse tokenResponse) throws BusinessServiceException;

    List<WorkSpaceDTO> getAllWorkSpaces(TokenResponse tokenResponse) throws BusinessServiceException;

    WorkSpaceDetailDTO getWorkSpaceDetail(String workSpaceId, TokenResponse tokenResponse) throws BusinessServiceException;

    void updateWorkSpace(UpdateWorkSpaceDTO updateWorkSpaceDTO, TokenResponse tokenResponse) throws BusinessServiceException;

    void finishWorking(FinishWorkingRequestDTO finishWorkingRequestDTO, TokenResponse tokenResponse) throws BusinessServiceException;

    JiraIssue getJiraIssue(String username, String password, String jiraUrl, String issueKey) throws BusinessServiceException;
}
