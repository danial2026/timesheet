package com.project.timesheet.dto;

public enum Website {

    JIRA("1"),
    TODOIST("2"),
    NOTION("3");

    private final String code;

    Website(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}