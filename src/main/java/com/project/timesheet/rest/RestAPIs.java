package com.project.timesheet.rest;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.dto.*;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.service.workSpace.WorkSpaceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestAPIs {

    private final WorkSpaceService workSpaceService;

    /**
     * @return
     * @throws BusinessServiceException
     */
    @GetMapping("/login")
    public ResponseEntity<LoginResponseDTO> login() throws BusinessServiceException {

        return ResponseEntity.ok(workSpaceService.login());
    }

    /**
     * @param authenticateCodeRequestDTO
     * @return
     * @throws BusinessServiceException
     */
    @PostMapping("/authenticate-code")
    public ResponseEntity<LoginDTO> authenticateCode(@RequestBody AuthenticateCodeRequestDTO authenticateCodeRequestDTO) throws BusinessServiceException {

        return ResponseEntity.ok(workSpaceService.authenticateCode(authenticateCodeRequestDTO.getCode()));
    }

    /**
     * @param accessToken
     * @param refreshToken
     * @param expiresInSeconds
     * @param clientId
     * @param CreateSheetDTO
     * @return
     * @throws BusinessServiceException
     */
    @PostMapping("/create-sheet")
    public ResponseEntity<String> createSheet(@RequestHeader(name = "Google-Sheet-Access-Token") String accessToken,
                                              @RequestHeader(name = "Google-Sheet-Refresh-Token") String refreshToken,
                                              @RequestHeader(name = "Google-Sheet-Expires-In-Seconds") Long expiresInSeconds,
                                              @RequestHeader(name = "Google-Sheet-ClientId") String clientId,
                                              @RequestBody CreateSheetDTO CreateSheetDTO
    ) throws BusinessServiceException {

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(expiresInSeconds);

        return ResponseEntity.ok(workSpaceService.createDefaultSheet(CreateSheetDTO.getSpreadSheetId(), CreateSheetDTO.getSheetTitle(), tokenResponse, clientId));
    }

    /**
     * @param jiraEmail
     * @param jiraApiToken
     * @param jiraUrl
     * @param issueKey
     * @return
     * @throws BusinessServiceException
     */
    @GetMapping("/get-jira-issue")
    public ResponseEntity<JiraIssue> getJiraIssue(@RequestHeader(name = "Jira-Email") String jiraEmail,
                                                  @RequestHeader(name = "Jira-Api-Token") String jiraApiToken,
                                                  @RequestHeader(name = "Jira-URL") String jiraUrl,
                                                  @RequestParam(name = "issueKey") String issueKey
    ) throws BusinessServiceException {

        return ResponseEntity.ok(workSpaceService.getJiraIssue(jiraEmail, jiraApiToken, jiraUrl, issueKey));
    }

    /**
     * @param accessToken
     * @param refreshToken
     * @param expiresInSeconds
     * @param clientId
     * @param finishWorkingRequestDTO
     * @return
     * @throws BusinessServiceException
     */
    @PutMapping("/finish-working")
    public ResponseEntity<Void> finishWorking(@RequestHeader(name = "Google-Sheet-Access-Token") String accessToken,
                                              @RequestHeader(name = "Google-Sheet-Refresh-Token") String refreshToken,
                                              @RequestHeader(name = "Google-Sheet-Expires-In-Seconds") Long expiresInSeconds,
                                              @RequestHeader(name = "Google-Sheet-ClientId") String clientId,
                                              @RequestBody FinishWorkingRequestDTO finishWorkingRequestDTO
    ) throws BusinessServiceException {

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(expiresInSeconds);

        workSpaceService.finishWorking(finishWorkingRequestDTO, tokenResponse, clientId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param accessToken
     * @param refreshToken
     * @param expiresInSeconds
     * @param clientId
     * @param taskId
     * @return
     * @throws BusinessServiceException
     */
    @PutMapping("/stop-working")
    public ResponseEntity<Void> stopWorking(@RequestHeader(name = "Google-Sheet-Access-Token") String accessToken,
                                            @RequestHeader(name = "Google-Sheet-Refresh-Token") String refreshToken,
                                            @RequestHeader(name = "Google-Sheet-Expires-In-Seconds") Long expiresInSeconds,
                                            @RequestHeader(name = "Google-Sheet-ClientId") String clientId,
                                            @RequestParam(name = "taskId") String taskId
    ) throws BusinessServiceException {

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(expiresInSeconds);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param accessToken
     * @param refreshToken
     * @param expiresInSeconds
     * @param clientId
     * @param createWorkSpaceDTO
     * @return
     * @throws BusinessServiceException
     */
    @PostMapping("/work-space")
    public ResponseEntity<Void> addWorkSpace(@RequestHeader(name = "Google-Sheet-Access-Token") String accessToken,
                                             @RequestHeader(name = "Google-Sheet-Refresh-Token") String refreshToken,
                                             @RequestHeader(name = "Google-Sheet-Expires-In-Seconds") Long expiresInSeconds,
                                             @RequestHeader(name = "Google-Sheet-ClientId") String clientId,
                                             @RequestBody CreateWorkSpaceDTO createWorkSpaceDTO
    ) throws BusinessServiceException {

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(expiresInSeconds);

        workSpaceService.createWorkSpace(createWorkSpaceDTO, tokenResponse, clientId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param accessToken
     * @param refreshToken
     * @param expiresInSeconds
     * @param clientId
     * @param updateWorkSpaceDTO
     * @return
     * @throws BusinessServiceException
     */
    @PutMapping("/work-space")
    public ResponseEntity<Void> updateWorkSpace(@RequestHeader(name = "Google-Sheet-Access-Token") String accessToken,
                                                @RequestHeader(name = "Google-Sheet-Refresh-Token") String refreshToken,
                                                @RequestHeader(name = "Google-Sheet-Expires-In-Seconds") Long expiresInSeconds,
                                                @RequestHeader(name = "Google-Sheet-ClientId") String clientId,
                                                @RequestBody UpdateWorkSpaceDTO updateWorkSpaceDTO
    ) throws BusinessServiceException {

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(expiresInSeconds);

        workSpaceService.updateWorkSpace(updateWorkSpaceDTO, tokenResponse, clientId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param email
     * @return
     * @throws BusinessServiceException
     */
    @GetMapping("/work-spaces-list")
    public ResponseEntity<List<WorkSpaceDTO>> getWorkSpacesList(@RequestParam(name = "email") String email) throws BusinessServiceException {

        return ResponseEntity.ok(workSpaceService.getAllWorkSpaces(email));
    }

    /**
     * @param accessToken
     * @param refreshToken
     * @param expiresInSeconds
     * @param clientId
     * @param workSpaceId
     * @return
     * @throws BusinessServiceException
     */
    @GetMapping("/work-space")
    public ResponseEntity<WorkSpaceDetailDTO> getWorkSpace(@RequestHeader(name = "Google-Sheet-Access-Token") String accessToken,
                                                           @RequestHeader(name = "Google-Sheet-Refresh-Token") String refreshToken,
                                                           @RequestHeader(name = "Google-Sheet-Expires-In-Seconds") Long expiresInSeconds,
                                                           @RequestHeader(name = "Google-Sheet-ClientId") String clientId,
                                                           @RequestParam(name = "workSpaceId") String workSpaceId
    ) throws BusinessServiceException {

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(expiresInSeconds);

        return ResponseEntity.ok(workSpaceService.getWorkSpaceDetail(workSpaceId, tokenResponse, clientId));
    }
}
