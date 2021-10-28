package com.project.timesheet.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Data
@Slf4j
public class APIError {
    private HttpStatus status;
    private Object code;
    private String message;


    private APIError(ErrorCode errorCode, String message) {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.code = errorCode.getCode();
        this.message = message;
    }

    APIError(ErrorCode errorCode) {
        this(errorCode, errorCode.name());
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "status=" + status +
                ", code=" + code +
                ", message=" + message +
                '}';
    }

}