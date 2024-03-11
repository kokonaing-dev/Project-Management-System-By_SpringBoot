package com.demo.project_management_system.repository;

import com.demo.project_management_system.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Method to find a project by projectName
    Optional<Project> findByProjectName(String projectName);

    @Query("SELECT MAX(p.id) FROM Project p")
    String findHighestId();


    void deleteById(long projectId);

    @Query("SELECT COUNT(issue) FROM Issue issue WHERE issue.project.id = :projectId")
    int findNumberOfIssuesByProjectId(@Param("projectId") long projectId);

    // Method to find projects by user ID
    @Query("SELECT p FROM Project p JOIN p.users u WHERE u.id = :userId")
    Set<Project> findProjectsByUserId(@Param("userId") long userId);
    Optional<Project> findById(long projectId);

    Project findProjectById(long id);

    List<Project> findByStatus(String status);
}
