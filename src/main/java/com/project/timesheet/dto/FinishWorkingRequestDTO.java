package com.project.timesheet.dto;

import lombok.Data;

@Data
public class FinishWorkingRequestDTO {

    private long timeInSec;

    private String taskId;

    private String workSpaceId;

    private String sheetId;
}