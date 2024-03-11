package com.demo.project_management_system.controller.api;

import com.demo.project_management_system.entity.Notification;
import com.demo.project_management_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationApi {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam Long userId) {
        List<Notification> notifications = notificationService.getNotificationByUserId(userId);

        return ResponseEntity.ok(notifications);
    }

}
