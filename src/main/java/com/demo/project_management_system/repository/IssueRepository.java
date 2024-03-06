package com.demo.project_management_system.repository;

import com.demo.project_management_system.entity.Issue;
import com.demo.project_management_system.entity.IssueType;
import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByPlanStartDateOrPlanDueDate(LocalDate todayDateOne, LocalDate todayDateTwo);

    int countIssuesByProjectId(long projectId);

    Set<Issue> findByProjectId(long projectId);

    List<Issue> findByUsersId(long id);

    boolean existsByProjectAndIssueTypeAndSubject(Project project, IssueType issueType, String subject);
}
