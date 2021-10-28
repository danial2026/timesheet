package com.project.timesheet.dto;

import lombok.Data;

@Data
public class UpdateWorkSpaceDTO {

    private String workSpaceId;

    private String workSpaceTitle;

    private String spreadSheetId;

    private String sheetId;

    private String email;
}