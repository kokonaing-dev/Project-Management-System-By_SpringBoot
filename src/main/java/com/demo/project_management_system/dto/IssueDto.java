package com.demo.project_management_system.dto;

import com.demo.project_management_system.entity.IssueStatus;
import com.demo.project_management_system.entity.IssueType;
import com.demo.project_management_system.entity.Priority;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class IssueDto {
        private Long id;
        private String subject;
        private String description;
        private IssueStatus issueStatus;
        private Priority priority;
        private LocalDate planStartDate;
        private LocalDate planDueDate;
        private LocalDate actualStartDate;
        private LocalDate actualEndDate;
        private int status;
        private List<String> attachmentFileNames;
        private LocalDate createdAt;
        private String projectName;
        private String issueName;
    }
