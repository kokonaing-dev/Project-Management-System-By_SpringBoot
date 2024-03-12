package com.demo.project_management_system.controller.api;

import com.demo.project_management_system.dto.NotificationDTO;
import com.demo.project_management_system.entity.Notification;
import com.demo.project_management_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class NotificationApi {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@RequestParam Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);

        // Convert List<Notification> to List<NotificationDTO> with IDs, projectIds, issueIds, and timestamps
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getContent(),
                        notification.getProject() != null ? notification.getProject().getId() : null,
                        notification.getIssue() != null ? notification.getIssue().getId() : null,
                        notification.getTimestamp()))
                .collect(Collectors.toList());

        log.info("Noti List " + notifications);
        return ResponseEntity.ok(notificationDTOs);
    }


}
