package com.project.timesheet.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class BusinessServiceException extends Exception {

    protected APIError apiError;

    public BusinessServiceException(ErrorCode errorCode) {
        apiError = new APIError(errorCode);
    }

    private String getStackTraceElementsMessage(String logMessage) {
        String stackTraceMessage = "";
        try {
            List<String> stackTraceElementsList =
                    Arrays.asList(getStackTrace())
                            .subList(1, 3)
                            .stream()
                            .map(StackTraceElement::getClassName)
                            .collect(Collectors.toList());
            Collections.reverse(stackTraceElementsList);
            stackTraceMessage = String.join(", ", stackTraceElementsList);
        } catch (Exception e) {
            //If happend any exception, return defined log message.
            stackTraceMessage = logMessage;
        }
        return stackTraceMessage;
    }

    private String convertErrorMessagesListToString(List<String> errorMessagesList) {
        if (errorMessagesList.isEmpty()) {
            return "";
        }

        return
                errorMessagesList
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",", "{", "}"));
    }
}