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
    @GetMapping("/get-user-info")
    public ResponseEntity<UserDTO> getUserInfo(@RequestHeader(name = "Authorization") String token) throws BusinessServiceException {


        System.out.println("/get-user-info");
        System.out.println(token);
        System.out.println();

        return ResponseEntity.ok(workSpaceService.getUserInfo(token));
    }

    /**
     * @return
     * @throws BusinessServiceException
     */
    @PostMapping("/create-sheet")
    public ResponseEntity<String> createSheet(@RequestHeader(name = "Authorization") String accessToken,
                                              @RequestBody CreateSheetDTO CreateSheetDTO
    ) throws BusinessServiceException {

        System.out.println("/create-sheet");
        System.out.println(accessToken);
        System.out.println(CreateSheetDTO);
        System.out.println();

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);

        return ResponseEntity.ok(workSpaceService.createDefaultSheet(CreateSheetDTO.getSpreadSheetId(), CreateSheetDTO.getSheetTitle(), tokenResponse));
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
     * @param finishWorkingRequestDTO
     * @return
     * @throws BusinessServiceException
     */
    @PutMapping("/finish-working")
    public ResponseEntity<Void> finishWorking(@RequestHeader(name = "Authorization") String accessToken,
                                              @RequestBody FinishWorkingRequestDTO finishWorkingRequestDTO
    ) throws BusinessServiceException {


        System.out.println("/finish-working");
        System.out.println(accessToken);
        System.out.println(finishWorkingRequestDTO);
        System.out.println();

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);

        workSpaceService.finishWorking(finishWorkingRequestDTO, tokenResponse);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param accessToken
     * @param taskId
     * @return
     * @throws BusinessServiceException
     */
    @PutMapping("/stop-working")
    public ResponseEntity<Void> stopWorking(@RequestHeader(name = "Authorization") String accessToken,
                                            @RequestParam(name = "taskId") String taskId
    ) throws BusinessServiceException {


        System.out.println("/stop-working");
        System.out.println(accessToken);
        System.out.println(taskId);
        System.out.println();

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param accessToken
     * @param createWorkSpaceDTO
     * @return
     * @throws BusinessServiceException
     */
    @PostMapping("/work-space")
    public ResponseEntity<Void> addWorkSpace(@RequestHeader(name = "Authorization") String accessToken,
                                             @RequestBody CreateWorkSpaceDTO createWorkSpaceDTO
    ) throws BusinessServiceException {


        System.out.println("post /work-space");
        System.out.println(accessToken);
        System.out.println(createWorkSpaceDTO);
        System.out.println();

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);

        workSpaceService.createWorkSpace(createWorkSpaceDTO, tokenResponse);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param accessToken
     * @param updateWorkSpaceDTO
     * @return
     * @throws BusinessServiceException
     */
    @PutMapping("put /work-space")
    public ResponseEntity<Void> updateWorkSpace(@RequestHeader(name = "Authorization") String accessToken,
                                                @RequestBody UpdateWorkSpaceDTO updateWorkSpaceDTO
    ) throws BusinessServiceException {

        System.out.println("/work-space");
        System.out.println(accessToken);
        System.out.println(updateWorkSpaceDTO);
        System.out.println();

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);

        workSpaceService.updateWorkSpace(updateWorkSpaceDTO, tokenResponse);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @return
     * @throws BusinessServiceException
     */
    @GetMapping("/work-spaces-list")
    public ResponseEntity<List<WorkSpaceDTO>> getWorkSpacesList(@RequestHeader(name = "Authorization") String accessToken) throws BusinessServiceException {

        System.out.println("/work-spaces-list");
        System.out.println(accessToken);
        System.out.println();

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);

        return ResponseEntity.ok(workSpaceService.getAllWorkSpaces(tokenResponse));
    }

    /**
     * @param accessToken
     * @param workSpaceId
     * @return
     * @throws BusinessServiceException
     */
    @GetMapping("/work-space")
    public ResponseEntity<WorkSpaceDetailDTO> getWorkSpace(@RequestHeader(name = "Authorization") String accessToken,
                                                           @RequestParam(name = "workSpaceId") String workSpaceId
    ) throws BusinessServiceException {

        System.out.println("get /work-space");
        System.out.println(accessToken);
        System.out.println(workSpaceId);
        System.out.println();

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(accessToken);

        return ResponseEntity.ok(workSpaceService.getWorkSpaceDetail(workSpaceId, tokenResponse));
    }
}
