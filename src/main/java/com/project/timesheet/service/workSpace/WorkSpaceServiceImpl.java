package com.project.timesheet.service.workSpace;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.dto.*;
import com.project.timesheet.entity.WorkSpaceEntity;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.exception.ErrorCode;
import com.project.timesheet.repository.WorkSpaceRepository;
import com.project.timesheet.service.sheet.SheetsIntegration;
import com.project.timesheet.service.sheet.SheetsServiceUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class  WorkSpaceServiceImpl implements WorkSpaceService{

    @Autowired
    private WorkSpaceRepository workSpaceRepository;

    @Autowired
    private SheetsIntegration sheetsIntegration;

    @Override
    public LoginDTO login() throws BusinessServiceException {
        LoginDTO loginDTO = new LoginDTO();

        try {
            Credential credential = SheetsServiceUtil.getCredential();

            loginDTO.setAccessToken(credential.getAccessToken());
            loginDTO.setRefreshToken(credential.getRefreshToken());
            loginDTO.setExpiresInSeconds(String.valueOf(credential.getExpiresInSeconds()));

            return loginDTO;
        } catch (Exception e) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }
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
        newWorkSpaceEntity.setSheetId(createWorkSpaceDTO.getSheetId());
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
        if (!updateWorkSpaceDTO.getSheetId().isEmpty()) {
            newWorkSpaceEntity.setSheetId(updateWorkSpaceDTO.getSheetId());
        }
        if (!updateWorkSpaceDTO.getEmail().isEmpty()) {
            newWorkSpaceEntity.setEmail(updateWorkSpaceDTO.getEmail());
        }

        workSpaceRepository.save(newWorkSpaceEntity);
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
        if (!sheetsIntegration.doesSheetExist(workSpaceEntity.getSpreadSheetId(), workSpaceEntity.getSheetId(), tokenResponse, clientId)) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        return convertEntityToWorkSpaceDetailDTO(workSpaceEntity);
    }

    private WorkSpaceDetailDTO convertEntityToWorkSpaceDetailDTO(WorkSpaceEntity workSpaceEntity){
        WorkSpaceDetailDTO workSpaceDetailDTO =  new WorkSpaceDetailDTO();
        workSpaceDetailDTO.setWorkSpaceId(workSpaceEntity.getId());
        workSpaceDetailDTO.setWorkSpaceTitle(workSpaceEntity.getWorkSpaceTitle());
        workSpaceDetailDTO.setSpreadSheetId(workSpaceEntity.getSpreadSheetId());
        workSpaceDetailDTO.setSheetId(workSpaceEntity.getSheetId());

        return workSpaceDetailDTO;
    }

    private WorkSpaceDTO convertEntityToWorkSpaceDTO(WorkSpaceEntity workSpaceEntity){
        WorkSpaceDTO workSpaceDTO =  new WorkSpaceDTO();
        workSpaceDTO.setWorkSpaceId(workSpaceEntity.getId());
        workSpaceDTO.setWorkSpaceTitle(workSpaceEntity.getWorkSpaceTitle());

        return workSpaceDTO;
    }
}
