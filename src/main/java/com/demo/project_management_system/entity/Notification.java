//package com.demo.project_management_system.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Date;
//import java.util.Set;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//public class Notification {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String content;
//
//    private Date timestamp;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private Set<User> users;
//
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    private Project project;
//
//    @ManyToOne
//    @JoinColumn(name = "issue_id")
//    private Issue issue;
//
//    @ManyToOne
//    @JoinColumn(name = "chat_message_id")
//    private ChatMessage chatMessage;
//
//}
