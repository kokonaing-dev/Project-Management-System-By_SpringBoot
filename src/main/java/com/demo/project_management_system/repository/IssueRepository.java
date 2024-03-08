package com.demo.project_management_system.repository;

import com.demo.project_management_system.entity.*;
import org.aspectj.weaver.patterns.TypeCategoryTypePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByPlanStartDateOrPlanDueDate(LocalDate todayDateOne, LocalDate todayDateTwo);

    int countIssuesByProjectId(long projectId);

    Set<Issue> findByProjectId(long projectId);

    List<Issue> findByUsersId(long id);

    boolean existsByProjectAndIssueTypeAndSubject(Project project, IssueType issueType, String subject);

    int countByIssueStatusAndProjectId(IssueStatus issueStatus, Long projectId);

    List<Issue> findByIssueType(IssueType issueType);

    List<Issue> findByCategory(Category category);
    @Query("SELECT COUNT(issue) FROM Issue issue WHERE issue.issueType.id = :issueTypeId AND issue.issueStatus = :status")
    int countByIssueTypeIdAndIssueStatus(@Param("issueTypeId") Long issueTypeId, @Param("status") IssueStatus status);

    @Query("SELECT it.issueName, COUNT(issue) " +
            "FROM Issue issue " +
            "JOIN issue.issueType it " +
            "GROUP BY it.issueName")
    List<Object[]> countByIssueTypeGroupByIssueTypeName();

    @Query("SELECT COUNT(issue) FROM Issue issue WHERE issue.category.id= :categoryId AND issue.issueStatus = :status")
    int countByCategoryIdAndStatus(@Param("categoryId") Long categoryId, @Param("status") IssueStatus status);

    @Query("SELECT c.categoryName , COUNT(issue) " +
            "FROM Issue issue " +
            "JOIN issue.category c " +
            "GROUP BY c.categoryName")
    List<Object[]> countByCategoryGroupByCategoryName();
}

