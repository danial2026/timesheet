package com.project.timesheet.exception;

public enum ErrorCode {

    NOT_AUTHORIZED("2001"),
    NOT_ALLOWED("2002"),
    EMAIL_ALREADY_EXIST("2003"),
    INVALID_EMAIL("2004"),
    USER_IS_NOT_FOUND("2005"),
    VALUE_IS_NULL("2006"),
    NOT_FOUND("2007"),
    INVALID_VALUE("2008"),
    ALREADY_EXIST("2009");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}