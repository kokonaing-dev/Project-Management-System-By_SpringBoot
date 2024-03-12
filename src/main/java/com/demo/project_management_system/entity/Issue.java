package com.demo.project_management_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Long id;

    private String subject;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.ORDINAL)
    private IssueStatus issueStatus = IssueStatus.OPEN; // Default value is OPEN

    @Enumerated(EnumType.ORDINAL)
    private Priority priority;

    private LocalDate planStartDate;
    private LocalDate planDueDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    private int status; //for soft delete

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> attachmentFileNames = new ArrayList<>();


    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", issueStatus=" + issueStatus +
                ", priority=" + priority +
                ", planStartDate=" + planStartDate +
                ", planDueDate=" + planDueDate +
                ", actualStartDate=" + actualStartDate +
                ", actualEndDate=" + actualEndDate +
                ", status=" + status +
                ", attachmentFileNames=" + attachmentFileNames +
                ", createdAt=" + createdAt +
                ", issueType=" + issueType +
                ", category=" + category +
                '}';
    }


//    @Transient // Skip persisting this field to the database
//    private String attachments;
//    private long size;
//    private byte[] content;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;

    @CreationTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "issue_type_id") // This is the foreign key column in the issues table for IssueType
    private IssueType issueType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id") // This is the foreign key column in the issues table for Category
    private Category category;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_issue",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    private Project project;

    @JsonIgnore
    //Issue Entity
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
