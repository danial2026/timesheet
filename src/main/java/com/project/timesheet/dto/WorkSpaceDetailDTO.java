package com.project.timesheet.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkSpaceDetailDTO {

    private String workSpaceId;

    private String workSpaceTitle;

    private String spreadSheetId;

    private List<SheetDTO> sheets;

    public void addSheet(SheetDTO sheet){
        sheets.add(sheet);
    }
}