package com.demo.project_management_system.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UpdateIssueStatusRequest {
    private String issueId;
    private String newIssueStatus;

}
