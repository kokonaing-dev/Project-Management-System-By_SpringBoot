package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.Category;
import com.demo.project_management_system.entity.Issue;
import com.demo.project_management_system.entity.IssueStatus;
import com.demo.project_management_system.entity.IssueType;
import com.demo.project_management_system.repository.CategoryRepository;
import com.demo.project_management_system.repository.IssueRepository;
import com.demo.project_management_system.repository.IssueTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueTypeService {

    @Autowired
    private IssueTypeRepository issueTypeRepository;


    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public void save(IssueType issueType) {
        // Perform any necessary validation or business logic
        issueTypeRepository.save(issueType);
    }

    public List<IssueType> getAllIssueTypes() {
        return issueTypeRepository.findAll();
    }

    public IssueType getIssueTypeById(long id) {
        return issueTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Issue Type not found with id: " + id));
    }

    public void deleteIssueTypeById(long id) {
        issueTypeRepository.deleteById(id);
    }



    public Map<IssueType, Set<IssueStatus>> getIssueTypeStatusMapping() {
        Map<IssueType, Set<IssueStatus>> issueTypeStatusMapping = new HashMap<>();

        // Fetch all issue types
        List<IssueType> issueTypes = issueTypeRepository.findAll();

        // Iterate through issue types
        for (IssueType issueType : issueTypes) {
            // Fetch all issues associated with this issue type
            List<Issue> issues = issueRepository.findByIssueType(issueType);

            // Collect the statuses of these issues
            Set<IssueStatus> statuses = issues.stream()
                    .map(Issue::getIssueStatus)
                    .collect(Collectors.toSet());

            // Add the issue type to the map with its associated statuses
            issueTypeStatusMapping.put(issueType, statuses);
        }

        return issueTypeStatusMapping;
    }


}

