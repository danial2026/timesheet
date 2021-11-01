package com.project.timesheet.service.jira;

import com.project.timesheet.dto.JiraIssue;
import com.project.timesheet.exception.BusinessServiceException;

public interface JiraClient {

    /**
     * @param issueKey
     * @return
     * @throws BusinessServiceException
     */
    JiraIssue getIssue(String issueKey) throws BusinessServiceException;
}
