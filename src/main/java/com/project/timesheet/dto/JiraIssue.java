package com.project.timesheet.dto;


import javax.annotation.Nullable;

public class JiraIssue {

    private String status;
    private String issueType;
    private String summary;
    @Nullable
    private String description;

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
        result += "\"description\": \"" + this.description + "\", ";
        result += "\"summary\": \"" + this.summary + "\"}";
        return result;
    }
}