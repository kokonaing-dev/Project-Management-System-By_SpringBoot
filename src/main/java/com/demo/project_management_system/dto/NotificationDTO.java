package com.demo.project_management_system.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private String content;
    private Long projectId;
    private Long issueId;
    private Timestamp timestamp;

    // Constructors, getters, and setters
}



