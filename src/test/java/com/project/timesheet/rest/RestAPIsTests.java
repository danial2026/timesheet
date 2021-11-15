package com.project.timesheet.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.entity.WorkSpaceEntity;
import com.project.timesheet.repository.WorkSpaceRepository;
import com.project.timesheet.service.sheet.SheetsIntegration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class RestAPIsTests {

    @MockBean
    private SheetsIntegration sheetsIntegrationMock;

    @MockBean
    private WorkSpaceRepository workSpaceRepositoryMock;

    @Autowired
    private MockMvc mvc;

    @Test
    public void updateStartWorkingTime_thenStatus200() throws Exception {

//        when(sheetsIntegrationMock.isAuthorized(anyString(), any(), anyString()))
//                .thenReturn(true);

//        this.mvc.perform(put("/api/v1/start-working")
//                .content("taskId=test")
//                .characterEncoding("UTF-8")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
//                .andExpect(status().isOk());
    }

    @Test
    public void updateStopWorkingTime_thenStatus200() throws Exception {

//        when(sheetsIntegrationMock.isAuthorized(anyString(), any(), anyString()))
//                .thenReturn(true);

//        this.mvc.perform(put("/api/v1/stop-working")
//                .content("taskId=test")
//                .characterEncoding("UTF-8")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
//                .andExpect(status().isOk());
    }

    @Test
    public void getWorkSpacesList_thenStatus200() throws Exception {
        HttpHeaders headers = new HttpHeaders();

        headers.add("Google-Sheet-Access-Token", "test");
        headers.add("Google-Sheet-Refresh-Token", "test");
        headers.add("Google-Sheet-Expires-In-Seconds", "0");
        headers.add("Google-Sheet-ClientId", "test");

        when(sheetsIntegrationMock.isAuthorized(anyString(), any()))
                .thenReturn(true);

        this.mvc.perform(get("/api/v1/work-spaces-list")
                .headers(headers)
                .param("email", "email")
        )
                .andExpect(status().isOk());
    }

    @Test
    public void getWorkSpace_thenStatus200() throws Exception {
        HttpHeaders headers = new HttpHeaders();

        headers.add("Google-Sheet-Access-Token", "test");
        headers.add("Google-Sheet-Refresh-Token", "test");
        headers.add("Google-Sheet-Expires-In-Seconds", "0");
        headers.add("Google-Sheet-ClientId", "test");


        WorkSpaceEntity workSpaceEntity = new WorkSpaceEntity();
        workSpaceEntity.setId("workSpaceId");
        workSpaceEntity.setWorkSpaceTitle("workSpaceTitle");
        workSpaceEntity.setSpreadSheetId("spreadSheetId");

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("test");
        tokenResponse.setRefreshToken("test");
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(0L);

        when(sheetsIntegrationMock.isAuthorized(anyString(), any()))
                .thenReturn(true);

        when(sheetsIntegrationMock.doesSpreedSheetExist(workSpaceEntity.getSpreadSheetId(), tokenResponse))
                .thenReturn(true);

        when(workSpaceRepositoryMock.findById(workSpaceEntity.getId()))
                .thenReturn(Optional.of(workSpaceEntity));

        this.mvc.perform(get("/api/v1/work-space")
                .param("workSpaceId", "workSpaceId")
                .headers(headers)
        )
                .andExpect(status().isOk());
    }

    @Test
    void contextLoads() {
    }

    private String convertObjectToJsonString(Object object) throws JsonProcessingException {
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        return mapper.writeValueAsString(object);
    }
}
