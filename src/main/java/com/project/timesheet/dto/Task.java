package com.project.timesheet.dto;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class Task {

    private String taskId;

    private String url;

    private Website website;

    private String name;

    private String dueDate;

    private String status;

    @Nullable
    private String taskType;

    @Nullable
    private String summary;

    @Nullable
    private String description;

    public Task(String taskId, String url, @Nullable Website website, String name, String dueDate, @Nullable String status, @Nullable String taskType, @Nullable String summary, @Nullable String description) {
        this.taskId = taskId;
        this.url = url;
        this.website = website;
        this.name = name;
        this.dueDate = dueDate;
        this.status = status;
        this.taskType = taskType;
        this.summary = summary;
        this.description = description;
    }
}