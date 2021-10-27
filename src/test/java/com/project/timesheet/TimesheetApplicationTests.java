package com.project.timesheet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.project.timesheet.dto.WorkSpaceDetailDTO;
import com.project.timesheet.entity.WorkSpaceEntity;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.repository.WorkSpaceRepository;
import com.project.timesheet.rest.RestAPIs;
import com.project.timesheet.service.sheet.SheetsIntegrationImpl;
import com.project.timesheet.service.workSpace.WorkSpaceService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@AutoConfigureMockMvc
@SpringBootTest
class TimesheetApplicationTests {

	@Autowired
	private RestAPIs restAPIs;

	@Autowired
	private WorkSpaceService workSpaceService;

	@MockBean
	private WorkSpaceService workSpaceServiceMock;

	@MockBean
	private SheetsIntegrationImpl sheetsIntegrationImplMock;

	@MockBean
	private WorkSpaceRepository workSpaceRepositoryMock;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private SheetsIntegrationImpl sheetsIntegrationImpl;

	@Before
	public void setUp() throws Exception {
		WorkSpaceEntity workSpaceEntity = new WorkSpaceEntity();
		workSpaceEntity.setId("workSpaceId");
		workSpaceEntity.setWorkSpaceTitle("workSpaceTitle");
		workSpaceEntity.setSpreadSheetId("spreadSheetId");

		Mockito.when(workSpaceRepositoryMock.findById(workSpaceEntity.getId()))
				.thenReturn(Optional.of(workSpaceEntity));

		RestAPIs restAPIs = new RestAPIs(workSpaceService); // 1
		this.mvc = MockMvcBuilders.standaloneSetup(restAPIs).build(); // 2
	}

	@Test
	public void sheetsIntegrationImpl_mainFunc() throws Exception {

	}

	@Test
	public void sheetsIntegrationImpl_updateSheetValue() throws Exception {
		TokenResponse tokenResponse = new TokenResponse();
		String scope = SheetsScopes.SPREADSHEETS;

        tokenResponse.setAccessToken("");
        tokenResponse.setRefreshToken("");
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(Long.valueOf("0"));
		tokenResponse.setScope(scope);

		String spreadSheetId = "1txtgLUf7TQwZkc_kYcWqbuBuVKLAKNmNydpIQH-6nNE";

		sheetsIntegrationImpl.updateSheetValue(spreadSheetId,"Sheet1", "E15", "test", tokenResponse);
	}

	@Test
	public void updateStartWorkingTime_thenStatus200() throws Exception {

		this.mvc.perform(put("/api/v1/start-working")
				.content("taskId=test")
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
				.andExpect(status().isOk());
	}

	@Test
	public void updateStopWorkingTime_thenStatus200() throws Exception {

		this.mvc.perform(put("/api/v1/stop-working")
				.content("taskId=test")
				.characterEncoding("UTF-8")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
				.andExpect(status().isOk());
	}

	@Test
	public void getWorkSpacesList_thenStatus200() throws Exception {
		HttpHeaders headers = new HttpHeaders();

		headers.add("Google-Sheet-Access-Token", "");
		headers.add("Google-Sheet-Refresh-Token", "");
		headers.add("Google-Sheet-Expires-In-Seconds", "0");

		given(sheetsIntegrationImplMock.isAuthorized(anyString(), new TokenResponse()))
				.willReturn(true);

		this.mvc.perform(get("/api/v1/work-spaces-list")
				.headers(headers)
				.param("email", "email")
				)
				.andExpect(status().isOk());
	}

	@Test
	public void getWorkSpace_thenStatus200() throws Exception {
		HttpHeaders headers = new HttpHeaders();

		headers.add("Google-Sheet-Access-Token", "");
		headers.add("Google-Sheet-Refresh-Token", "");
		headers.add("Google-Sheet-Expires-In-Seconds", "0");

		given(sheetsIntegrationImplMock.isAuthorized(anyString(), new TokenResponse()))
				.willReturn(true);

		this.mvc.perform(get("/api/v1/work-space")
				.param("workSpaceId", "61782e8a8dab312b55c9ce92")
				.headers(headers)
				)
				.andExpect(status().isOk());
	}

	@Test
	public void whenValidNameAndWorkSpaceId_thenWorkSpaceShouldBeFound() throws BusinessServiceException {
		String workSpaceId = "workSpaceId";
		String username = "username";

		WorkSpaceDetailDTO workSpaceDetail = new WorkSpaceDetailDTO();

		WorkSpaceDetailDTO expectWorkSpaceDetail = new WorkSpaceDetailDTO();
		expectWorkSpaceDetail.setSheetId("sheetId");
		expectWorkSpaceDetail.setWorkSpaceTitle("workSpaceTitle");

		given(workSpaceService.getWorkSpaceDetail(workSpaceId, null))
				.willReturn(expectWorkSpaceDetail);

		try {
			workSpaceDetail = workSpaceService.getWorkSpaceDetail(workSpaceId, null);
		} catch (BusinessServiceException e) {
			fail(e.getMessage());
		}

		assertThat(workSpaceDetail.getWorkSpaceTitle())
				.isEqualTo("workSpaceTitle");
	}

	@Test
	void contextLoads() {
	}
}
