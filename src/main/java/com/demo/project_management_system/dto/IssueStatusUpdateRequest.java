package com.demo.project_management_system.dto;

import lombok.Data;
@Data
public class IssueStatusUpdateRequest {
    private Long issueId;
    private String newIssueStatus;
}
