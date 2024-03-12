package com.demo.project_management_system.dto;

import lombok.Data;

@Data
public class UserRoleUpdateRequest {
    private Long userId;
    private String selectedRole;
}
