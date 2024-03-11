package com.demo.project_management_system.dto;

import com.demo.project_management_system.entity.IssueStatus;
import com.demo.project_management_system.entity.Priority;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectDto {
    private Long id;
    private String projectName;
    private LocalDate projectStartDate;
    private LocalDate projectDueDate;
}
