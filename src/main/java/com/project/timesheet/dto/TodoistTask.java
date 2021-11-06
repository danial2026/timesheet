package com.project.timesheet.dto;


import lombok.Data;

import javax.annotation.Nullable;

@Data
public class TodoistTask {

    private String taskId;

    private String status;

    private String projectName;

    private String projectId;

    @Nullable
    private String summary;

    @Nullable
    private String description;

    @Nullable
    private String dueDate;

    public TodoistTask(String status, String summary, @Nullable String description) {
        this.status = status;
        this.summary = summary;
        this.description = description;
    }

    public TodoistTask() {
    }

    public String toString() {
        String result = "";
        result += "{\"taskId\": \"" + this.taskId + "\", ";
        result += "\"status\": \"" + this.status + "\", ";
        result += "\"projectName\": \"" + this.projectName + "\", ";
        result += "\"projectId\": \"" + this.projectId + "\", ";
        result += "\"summary\": \"" + this.summary + "\", ";
        result += "\"description\": \"" + this.description + "\", ";
        result += "\"dueDate\": \"" + this.dueDate + "\"}";
        return result;
    }
}