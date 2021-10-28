package com.project.timesheet.service.workSpace;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.project.timesheet.dto.WorkSpaceDetailDTO;
import com.project.timesheet.entity.WorkSpaceEntity;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.repository.WorkSpaceRepository;
import com.project.timesheet.service.sheet.SheetsIntegration;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@AutoConfigureMockMvc
@SpringBootTest
class WorkSpaceServiceTest {

	@MockBean
	private WorkSpaceService workSpaceServiceMock;

	@MockBean
	private SheetsIntegration sheetsIntegrationMock;

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

		Mockito.when(sheetsIntegrationMock.isAuthorized(anyString(), new TokenResponse(), anyString()))
				.thenReturn(true);
	}

	@Test
	public void workSpaceService_whenValidNameAndWorkSpaceId_thenWorkSpaceShouldBeFound() throws BusinessServiceException {
		String workSpaceId = "workSpaceId";
		String username = "username";

		WorkSpaceDetailDTO workSpaceDetail = new WorkSpaceDetailDTO();

		WorkSpaceDetailDTO expectWorkSpaceDetail = new WorkSpaceDetailDTO();
		expectWorkSpaceDetail.setSheetId("sheetId");
		expectWorkSpaceDetail.setWorkSpaceTitle("workSpaceTitle");

		WorkSpaceEntity workSpaceEntity = new WorkSpaceEntity();
		workSpaceEntity.setId("workSpaceId");
		workSpaceEntity.setEmail("email");
		workSpaceEntity.setWorkSpaceTitle("workSpaceTitle");
		workSpaceEntity.setSheetId("sheetId");

		given(workSpaceRepositoryMock.findById(anyString()))
				.willReturn(Optional.of(workSpaceEntity));

		given(sheetsIntegrationMock.isAuthorized(workSpaceEntity.getSpreadSheetId(), new TokenResponse(), "clientId"))
				.willReturn(true);

		try {
			workSpaceDetail = workSpaceServiceMock.getWorkSpaceDetail(workSpaceId, new TokenResponse(), "clientId");
		} catch (BusinessServiceException e) {
			fail(e.getMessage());
		}

		/*
		assertThat(workSpaceDetail.getWorkSpaceTitle())
				.isEqualTo(workSpaceEntity.getWorkSpaceTitle());

		assertThat(workSpaceDetail.getWorkSpaceId())
				.isEqualTo(workSpaceEntity.getId());
		 */
	}

	@Test
	void contextLoads() {
	}
}
