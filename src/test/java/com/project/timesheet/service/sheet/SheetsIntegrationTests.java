package com.project.timesheet.service.sheet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.project.timesheet.entity.WorkSpaceEntity;
import com.project.timesheet.repository.WorkSpaceRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

@AutoConfigureMockMvc
@SpringBootTest
class SheetsIntegrationTests {

	@MockBean
	private WorkSpaceRepository workSpaceRepositoryMock;

	@Before
	public void setUp() throws Exception {
		WorkSpaceEntity workSpaceEntity = new WorkSpaceEntity();
		workSpaceEntity.setId("workSpaceId");
		workSpaceEntity.setWorkSpaceTitle("workSpaceTitle");
		workSpaceEntity.setSpreadSheetId("spreadSheetId");

		Mockito.when(workSpaceRepositoryMock.findById(workSpaceEntity.getId()))
				.thenReturn(Optional.of(workSpaceEntity));
	}

	@Test
	public void sheetsIntegrationImpl_getSheetTitles() throws Exception {

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

		String spreadSheetId = "";

		// DEV : can only be tested with google tokens so need to be tested manually
		// sheetsIntegrationImpl.updateSheetValue(spreadSheetId,"Sheet1", "E15", "test", tokenResponse);
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
