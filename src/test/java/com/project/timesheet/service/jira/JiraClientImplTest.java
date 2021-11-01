package com.project.timesheet.service.jira;

import com.project.timesheet.exception.BusinessServiceException;
import org.junit.jupiter.api.Test;

class JiraClientImplTest {

    @Test
    void getIssue() throws BusinessServiceException {

        // get api token from https://id.atlassian.com/manage-profile/security/api-tokens
        JiraClientImpl client = new JiraClientImpl("your-email@gmail.com", "<API-Token>", "https://ibook.atlassian.net");
        client.getIssue("<issue-key>").toString();
    }
}