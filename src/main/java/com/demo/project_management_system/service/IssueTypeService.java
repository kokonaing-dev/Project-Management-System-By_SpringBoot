package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.IssueType;
import com.demo.project_management_system.repository.IssueTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class IssueTypeService {

    @Autowired
    private IssueTypeRepository issueTypeRepository;

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
}
