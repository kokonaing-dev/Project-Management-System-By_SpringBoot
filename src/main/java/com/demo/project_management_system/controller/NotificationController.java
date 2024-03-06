//package com.demo.project_management_system.controller;
//
//import com.demo.project_management_system.dto.NotificationRequest;
//import com.demo.project_management_system.entity.Notification;
//import com.demo.project_management_system.entity.Project;
//import com.demo.project_management_system.entity.User;
//import com.demo.project_management_system.repository.ProjectRepository;
//import com.demo.project_management_system.repository.UserRepository;
//import com.demo.project_management_system.service.NotificationService;
//import com.demo.project_management_system.service.ProjectService;
//import com.demo.project_management_system.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.simp.annotation.SendToUser;
//import org.springframework.stereotype.Controller;
//
//import java.util.Set;
//
//@Controller
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final SimpMessagingTemplate messagingTemplate;
//    private final NotificationService notificationService;
//    private final ProjectService projectService;
//    private final UserService userService ;
//
////    @MessageMapping("/{projectId}/sendNotiToProject")
////    @SendTo("/topic/{projectId}/notification")
////    public Notification handleNotification(@Payload Notification notification, @DestinationVariable("projectId") String projectId) {
////        System.out.println("notification to project");
////        return notification;
////    }
////
////    @MessageMapping("/public")
////    @SendTo("/topic/public")
////    public Notification handleNotificationforPublic(@Payload Notification notification) {
////        System.out.println("that is public level");
////        return notification;
////    }
//
//    @MessageMapping("/specific")
//    public void handleNotificationToSpecificUsers(@Payload NotificationRequest request) {
//        Project project = projectService.getProjectById(request.getProjectId());
//        System.out.println("project : " + project);
//        Set<User> users = userService.findByIdIn(request.getUserIds());
//        System.out.println("users : " + users);
//
//        System.out.println("i already sent notification to specific");
//        // Save notification
//        Notification notification = new Notification();
//        notification.setContent(request.getContent());
//        notification.setProject(project);
//        notification.setUsers(users);
//        notificationService.saveNotification(notification);
//
//        // Send notifications to users
//        for (User user : users) {
//            String userDestination = "/user/" + user.getUsername() + "/topic/notifications";
//            messagingTemplate.convertAndSend(userDestination, notification);
//        }
//
//    }
//
//}
