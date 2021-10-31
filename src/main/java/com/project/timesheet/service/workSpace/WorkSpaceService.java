package com.project.timesheet.service.workSpace;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.dto.*;
import com.project.timesheet.exception.BusinessServiceException;

import java.util.List;

public interface WorkSpaceService {

    LoginResponseDTO login() throws BusinessServiceException;

    LoginDTO authenticateCode(String code) throws BusinessServiceException;

    String createDefaultSheet(String spreadSheetId, String sheetTitle, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void createWorkSpace(CreateWorkSpaceDTO createWorkSpaceDTO, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    List<WorkSpaceDTO> getAllWorkSpaces(String email) throws BusinessServiceException;

    WorkSpaceDetailDTO getWorkSpaceDetail(String workSpaceId, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void updateWorkSpace(UpdateWorkSpaceDTO updateWorkSpaceDTO, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;

    void finishWorking(FinishWorkingRequestDTO finishWorkingRequestDTO, TokenResponse tokenResponse, String clientId) throws BusinessServiceException;
}
