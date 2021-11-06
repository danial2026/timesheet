package com.project.timesheet.dto;


import lombok.Data;

import javax.annotation.Nullable;

@Data
public class JiraIssue {

    private String status;
    private String issueType;
    @Nullable
    private String issueKey;
    private String summary;
    @Nullable
    private String description;
    @Nullable
    private String assignee;

    public JiraIssue(String status, String issueType, String issueKey, String summary, @Nullable String description) {
        this.status = status;
        this.issueType = issueType;
        this.issueKey = issueKey;
        this.summary = summary;
        this.description = description;
    }

    public JiraIssue(String status, String issueType, String summary, @Nullable String description) {
        this.status = status;
        this.issueType = issueType;
        this.summary = summary;
        this.description = description;
    }

    public JiraIssue() {
    }

    public String toString() {
        String result = "";
        result += "{\"status\": \"" + this.status + "\", ";
        result += "\"issueType\": \"" + this.issueType + "\", ";
        result += "\"issueKey\": \"" + this.issueKey + "\", ";
        result += "\"description\": \"" + this.description + "\", ";
        result += "\"assignee\": \"" + this.assignee + "\", ";
        result += "\"summary\": \"" + this.summary + "\"}";
        return result;
    }
}