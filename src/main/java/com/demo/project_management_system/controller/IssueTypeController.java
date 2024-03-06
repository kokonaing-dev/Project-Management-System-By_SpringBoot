package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.IssueType;
import com.demo.project_management_system.service.IssueTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class IssueTypeController {

    @Autowired
    private IssueTypeService issueTypeService;

    @PostMapping("/saveIssueType")
    public String saveIssueType(@Valid @ModelAttribute("issueType") IssueType issueType, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/setting"; // Return to the form page with validation errors
        }
        issueTypeService.save(issueType);
        return "redirect:/setting"; // Redirect to the home page or any other appropriate page
    }

    @DeleteMapping("/deleteIssueType/{id}")
    public ResponseEntity<Void> deleteIssueType(@PathVariable("id") int id) {
        issueTypeService.deleteIssueTypeById(id);
        return ResponseEntity.noContent().build();
    }
}
