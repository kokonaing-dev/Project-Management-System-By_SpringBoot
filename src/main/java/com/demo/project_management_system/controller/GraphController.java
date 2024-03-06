package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.Issue;
import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.entity.User;
import com.demo.project_management_system.service.IssueService;
import com.demo.project_management_system.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class GraphController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueService issueService;

    @GetMapping("/displayBarGraph")
    public String barGraph(HttpSession session, Model model) {
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);

        // Retrieve the project by its ID
        Project project = projectService.getProjectById(2);

        // Retrieve the issues for the selected project
        Set<Issue> issueList = issueService.findIssueByProjectId(2);

        // Create a map to hold the completion percentages for each issue type
        Map<String, Double> issueTypeCompletionPercentageMap = new HashMap<>();

        // Count occurrences of each issue type and issue status
        Map<String, Map<String, Integer>> issueTypeStatusCountMap = new HashMap<>();
        for (Issue issue : issueList) {
            String issueType = issue.getIssueType().getIssueName();
            String issueStatus = String.valueOf(issue.getIssueStatus());
            // Get the inner map for the issue type or create a new one if it doesn't exist
            Map<String, Integer> statusCountMap = issueTypeStatusCountMap.getOrDefault(issueType, new HashMap<>());
            // Increment the count for the issue status
            statusCountMap.put(issueStatus, statusCountMap.getOrDefault(issueStatus, 0) + 1);
            // Update the inner map for the issue type
            issueTypeStatusCountMap.put(issueType, statusCountMap);
        }

        // Calculate completion percentage for each issue type
        for (Map.Entry<String, Map<String, Integer>> entry : issueTypeStatusCountMap.entrySet()) {
            String issueType = entry.getKey();
            Map<String, Integer> statusCountMap = entry.getValue();

            // Check if there are no occurrences of OPEN or IN_PROGRESS status
            if (!statusCountMap.containsKey("OPEN") && !statusCountMap.containsKey("IN_PROGRESS")) {
                // If no OPEN or IN_PROGRESS, set completion percentage to 100%
                issueTypeCompletionPercentageMap.put(issueType, 100.0);
            } else {
                // Calculate total count of issues for the current issue type
                int totalCount = statusCountMap.values().stream().mapToInt(Integer::intValue).sum();
                // Calculate completion percentage based on the specified values for each issue status
                double completionPercentage = (statusCountMap.getOrDefault("OPEN", 0) * 0.0
                        + statusCountMap.getOrDefault("IN_PROGRESS", 0) * 25.0
                        + statusCountMap.getOrDefault("SOLVED", 0) * 50.0) / totalCount;
                // Update the completion percentage for the issue type
                issueTypeCompletionPercentageMap.put(issueType, completionPercentage);
            }
        }

        model.addAttribute("issueTypeCompletionPercentageMap", issueTypeCompletionPercentageMap);
        return "barGraph";
    }

}

