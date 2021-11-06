package com.project.timesheet.service.jira;

import com.project.timesheet.dto.JiraIssue;
import com.project.timesheet.exception.BusinessServiceException;

import java.util.List;

public interface JiraClient {

    /**
     * @param issueKey
     * @return
     * @throws BusinessServiceException
     */
    JiraIssue getIssue(String issueKey) throws BusinessServiceException;

    List<JiraIssue> getIssuesAssignedToMe(String projectKey) throws BusinessServiceException;
}
