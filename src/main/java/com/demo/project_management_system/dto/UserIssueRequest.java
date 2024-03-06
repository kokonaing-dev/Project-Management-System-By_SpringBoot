package com.demo.project_management_system.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserIssueRequest {
    private Long issueId;
    private List<Long> selectedUsers;
}
