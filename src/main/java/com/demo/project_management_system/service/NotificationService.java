package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.Issue;
import com.demo.project_management_system.entity.Notification;
import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.entity.User;
import com.demo.project_management_system.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ProjectService projectService;

    public void sendNotification(Object object) {
        String content;
        Set<User> users;
        Long projectId = null;
        Long issueId = null;

        if (object instanceof Project project) {
            System.out.println("project");
            content = "You have been assigned to " + project.getProjectName();
            users = project.getUsers();
            projectId = project.getId(); // Assuming you have a method to get the project ID
            log.info("Project Id :" + projectId);
        } else if (object instanceof Issue issue) {
            System.out.println("issue");
            content = "New issue: " + issue.getDescription();
            users = issue.getProject().getUsers();
            projectId = issue.getProject().getId(); // Assuming you have a method to get the project ID
            issueId = issue.getId(); // Assuming you have a method to get the issue ID
            log.info("Issue Id :" + issueId);
        } else {
            // Handle other types of entities or throw an exception
            content = "New notification";
            users = new HashSet<>(); // or handle appropriately for your case
        }

        for (User user : users) {
            Notification notification = new Notification(); // Create a new Notification object for each user
            notification.setTimestamp(new Timestamp(System.currentTimeMillis()));
            notification.setContent(content);
            notification.setProjectId(projectId);
            notification.setIssueId(issueId);
            notification.setUser(user); // Set the User for the Notification

            notificationRepository.save(notification); // Save the notification for each user
            messagingTemplate.convertAndSendToUser(user.getEmail(), "/queue/notification", notification);
        }
    }

    public List<Notification> getNotificationByUserId(Long userId) {
        return notificationRepository.getNotificationByUserId(userId);
    }
}


