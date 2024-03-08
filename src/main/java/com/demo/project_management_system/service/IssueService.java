package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.Issue;
import com.demo.project_management_system.entity.IssueStatus;
import com.demo.project_management_system.entity.IssueType;
import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.repository.IssueRepository;
import com.demo.project_management_system.repository.IssueTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class IssueService {
    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueTypeRepository issueTypeRepository;
    public int getTotalIssuesByProjectId(Long projectId) {
        return issueRepository.countIssuesByProjectId(projectId);
    }

    public Issue save(Issue issue) {
        issueRepository.save(issue);
        return issue;
    }

    public Optional<Issue> findIssueById(long id) {
        return issueRepository.findById(id);
    }

    public Set<Issue> getAllIssues() {
        List<Issue> issueList = issueRepository.findAll();
        return new HashSet<>(issueList);
    }

    public void delete(long id) {
        issueRepository.deleteById(id);
    }

    public void updateIssue(Optional<Issue> existingIssueOptional) {
        if (existingIssueOptional.isPresent()) {
            Issue existingIssue = existingIssueOptional.get();
            issueRepository.save(existingIssue);
        } else {
            // Handle case where existingIssue is not present
            throw new IllegalArgumentException("Existing issue is not present");
        }
    }

    public Optional<Issue> findById(long issueId) {
        return issueRepository.findById(issueId);
    }

    public List<Issue> findByPlanStartDateOrPlanDueDate(LocalDate todayDateOne, LocalDate todayDateTwo) {
        // Delegate the call to the repository
        return issueRepository.findByPlanStartDateOrPlanDueDate(todayDateOne, todayDateTwo);
    }


    public Set<Issue> findIssueByProjectId(long id) {
        return issueRepository.findByProjectId(id);
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    public List<Issue> getIssuesByUserId(Long id) {
        return issueRepository.findByUsersId(id);
    }


    public Set<Issue> getAllIssuesByProjectId(long projectId) {
        return issueRepository.findByProjectId(projectId);
    }

    public boolean existsByProjectAndIssueTypeAndSubject(Project project, IssueType issueType, String subject) {
        return issueRepository.existsByProjectAndIssueTypeAndSubject(project, issueType, subject);
    }

    public int getIssuesCountByStatusAndProjectId(IssueStatus issueStatus, Long projectId) {
        return issueRepository.countByIssueStatusAndProjectId(issueStatus, projectId);
    }

    public int getIssueCountByTypeAndStatus(Long issueTypeId, IssueStatus status) {
        return issueRepository.countByIssueTypeIdAndIssueStatus(issueTypeId, status);
    }
    public List<Object[]> getIssueTypeData() {
        return issueRepository.countByIssueTypeGroupByIssueTypeName();
}

    public int getIssueCountByCategoryAndStatus(Long categoryId, IssueStatus status) {
        return issueRepository.countByIssueTypeIdAndIssueStatus(categoryId, status);
    }
    public List<Object[]> getCategoryData() {
        return issueRepository.countByCategoryGroupByCategoryName();
    }
}
