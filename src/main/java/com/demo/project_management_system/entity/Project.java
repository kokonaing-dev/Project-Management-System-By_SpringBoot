package com.demo.project_management_system.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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


    private LocalDate projectStartDate;


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

}
