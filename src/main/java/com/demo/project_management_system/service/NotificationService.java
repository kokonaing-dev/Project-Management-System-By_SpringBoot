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


    public void sendNotification(Object object) {
        String content;
        Set<User> users = new HashSet<>();
        Project project = null;
        Issue issue = null;

        if (object instanceof Project) {
            project = (Project) object;
            System.out.println("project");
            content = "You have been assigned to " + project.getProjectName();
            users = project.getUsers();
        } else if (object instanceof Issue) {
            issue = (Issue) object;
            System.out.println("issue");
            content = "New issue: " + issue.getDescription();
            users = issue.getProject().getUsers();
        } else {
            // Handle other types of entities or throw an exception
            content = "New notification";
            // You might handle other types of entities differently here
        }

        for (User user : users) {
            Notification notification = new Notification();
            notification.setTimestamp(new Timestamp(System.currentTimeMillis()));
            notification.setContent(content);
            notification.setProject(project);
            notification.setIssue(issue);
            notification.setUser(user);
            log.info("ProjectId in notification :" + notification.getProject().getId());
            notificationRepository.save(notification);
            messagingTemplate.convertAndSendToUser(user.getEmail(), "/queue/notification", notification);
        }
    }



    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}


