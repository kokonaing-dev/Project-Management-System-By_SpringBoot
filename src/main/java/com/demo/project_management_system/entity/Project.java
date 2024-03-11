package com.demo.project_management_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@RequiredArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(unique = true)
    private String projectName;

    @Column(unique = true)
    private String projectKey;

    @JsonIgnore
    private LocalDate projectStartDate;

    @JsonIgnore
    private LocalDate projectDueDate;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @ManyToMany(fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    @JoinTable(name = "user_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Set<ChatMessage> messages;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<Issue> issues;

    @Column(name = "status")
    private String status = "ACTIVE"; // Set default value here

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Transient
    private int numberOfIssues;

    public String calculateProjectStatus() {
        int totalIssues = issues.size();
        int inProgressCount = 0;
        int closedCount = 0;
        int solvedCount = 0;

        for (Issue issue : issues) {
            switch (issue.getIssueStatus()) {
                case IN_PROGRESS:
                    inProgressCount++;
                    break;
                case CLOSED:
                    closedCount++;
                    break;
                case SOLVED:
                    solvedCount++;
                    break;
                default:
                    // Do nothing or handle other statuses
                    break;
            }
        }

        // Determine project status based on the counts and other conditions
        if (inProgressCount == totalIssues) {
            return "Working in Progress";
        } else if (closedCount == totalIssues) {
            return "Done";
        } else if (inProgressCount > 0 || (closedCount > 0 && inProgressCount + closedCount < totalIssues)) {
            return "Working in Progress";
        } else if (solvedCount == totalIssues) {
            return "Pending";
        } else if (projectStartDate.isAfter(LocalDate.now())) {
            return "Coming Soon";
        } else {
            return "Other"; // Or any default status you want
        }
    }

}
