package com.demo.project_management_system.dto;

import lombok.Data;
@Data
public class IssuePriorityUpdateRequest {
    private Long issueId;
    private String newPriority;
}
