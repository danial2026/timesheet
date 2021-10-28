package com.project.timesheet.dto;

import lombok.Data;

@Data
public class CreateWorkSpaceDTO {

    private String workSpaceTitle;

    private String spreadSheetId;

    private String sheetId;

    private String email;
}