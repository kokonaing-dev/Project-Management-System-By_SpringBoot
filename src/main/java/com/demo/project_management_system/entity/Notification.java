package com.demo.project_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Timestamp timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private Long projectId;

    @Transient
    private Long issueId;


}
