package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.Category;
import com.demo.project_management_system.entity.Issue;
import com.demo.project_management_system.entity.IssueStatus;
import com.demo.project_management_system.entity.IssueType;
import com.demo.project_management_system.repository.CategoryRepository;
import com.demo.project_management_system.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

  
    private  final CategoryRepository categoryRepository;

    @Autowired
    private IssueRepository issueRepository;
    public void save(Category category) {
        categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));
    }

    public void deleteCategoryById(int id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> getAllIssueCategories() {
        return categoryRepository.findAll();
    }


    public Map<Category, Set<IssueStatus>> getCategoryStatusMapping() {
        Map<Category, Set<IssueStatus>> categoryStatusMapping = new HashMap<>();

        // Fetch all issue types
        List<Category> categories = categoryRepository.findAll();

        // Iterate through issue types
        for (Category category : categories) {
            // Fetch all issues associated with this issue type
            List<Issue> issues = issueRepository.findByCategory(category);

            // Collect the statuses of these issues
            Set<IssueStatus> statuses = issues.stream()
                    .map(Issue::getIssueStatus)
                    .collect(Collectors.toSet());

            // Add the issue type to the map with its associated statuses
            categoryStatusMapping.put(category, statuses);
        }

        return categoryStatusMapping;
    }
}
