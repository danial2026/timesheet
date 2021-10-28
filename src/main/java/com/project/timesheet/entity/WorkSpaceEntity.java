package com.project.timesheet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workSpace")
public class WorkSpaceEntity {

    @Id
    @Indexed(unique = true)
    private String id;

    @Field("email")
    @Indexed(unique = true)
    private String email;

    @Field("workSpace_title")
    private String workSpaceTitle;

    @Field("spreadSheet_id")
    private String spreadSheetId;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
}