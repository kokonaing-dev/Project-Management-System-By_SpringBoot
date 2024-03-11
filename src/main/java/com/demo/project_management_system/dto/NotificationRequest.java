package com.demo.project_management_system.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private boolean isUserConnected;
    private String userEmail;
    private Long userId;

    // Constructors, getters, and setters
}



