package com.demo.project_management_system.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotificationRequest {
    private String content;
    private Long projectId;
    private List<Long> userIds;

}
