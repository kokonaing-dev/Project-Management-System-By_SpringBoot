package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.*;
import com.demo.project_management_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class MemberController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueTypeService issueTypeService;

    @Autowired
    private CategoryService categoryService;

//    @GetMapping("/member/tasks")
//    public String memberTasks(HttpSession session , Model model){
//        // Retrieve the user object from the session
//        User loggedInUser = (User) session.getAttribute("loggedInUser");
//
//        // Pass the user object to the view
//        model.addAttribute("loggedInUser", loggedInUser);
//
//        int userId = loggedInUser.getId();
//        System.out.println("loggedInUserId......... " + userId);
//
//        // Find projects for the logged-in user
//        Set<Project> userProjects = userService.findProjectsByUserId(userId);
//        System.out.println("User Projects " + userProjects);
//
//        // List to store all issues
//        List<Issue> allIssues = new ArrayList<>();
//        List<Issue> upcomingIssues = new ArrayList<>();
//        List<Issue> otherTasks = new ArrayList<>();
//
//        model.addAttribute("allIssues", allIssues);
//        model.addAttribute("upcomingIssues", upcomingIssues);
//        model.addAttribute("otherTasks", otherTasks);
//
//        // Get today's date
//        LocalDate today = LocalDate.now();
//        System.out.println("TODAY DATE...." + today);
//
//        for (Project project : userProjects) {
//
//            int projectId = project.getId();
//
//            // Get issues for the current project
//            Set<Issue> issuesForProject = projectService.getIssuesByProjectId(projectId);
//
//            // Filter issues where planStartDate or planDueDate is equal to today's date
//            List<Issue> filteredTodayIssues = issuesForProject.stream()
//                    .filter(issue -> {
//                        LocalDate planStartDate = issue.getPlanStartDate();
//                        LocalDate planDueDate = issue.getPlanDueDate();
//                        return (planStartDate != null && planStartDate.equals(today)) ||
//                                (planDueDate != null && planDueDate.equals(today));
//                    })
//                    .toList();
//
//            // Add filtered issues for today to the list of all issues
//            allIssues.addAll(filteredTodayIssues);
//            System.out.println("User..........Issues" + allIssues);
//
//
//
//
//            // Filter issues where planStartDate or planDueDate is after today's date and the issue is not marked as SOLVED, CLOSED, or Overdue
//            List<Issue> filteredUpcomingIssues = issuesForProject.stream()
//                    .filter(issue -> {
//                        IssueStatus issueStatus = issue.getIssueStatus();
//                        LocalDate planStartDate = issue.getPlanStartDate();
//                        LocalDate planDueDate = issue.getPlanDueDate();
//                        LocalDate actualEndDate = issue.getActualEndDate();
//                        return !(issueStatus == IssueStatus.SOLVED || // Exclude solved issues
//                                issueStatus == IssueStatus.CLOSED || // Exclude closed issues
//                                (planDueDate != null && planDueDate.isBefore(today)) || // Exclude overdue based on planDueDate
//                                (actualEndDate != null && actualEndDate.isBefore(today))) && // Exclude overdue based on actualEndDate
//                                ((planStartDate == null || planStartDate.isAfter(today)) && // Include issues where planStartDate is after today
//                                        (planDueDate == null || planDueDate.isAfter(today))); // Include issues where planDueDate is after today
//                    })
//                    .toList();
//
//            // Add filtered upcoming issues
//            upcomingIssues.addAll(filteredUpcomingIssues);
//            System.out.println("User..........Upcoming Issues" + upcomingIssues);
//
//
//
//
//            // Filter issues for otherTasks
//            List<Issue> filteredOtherTasks = issuesForProject.stream()
//                    .filter(issue -> {
//                        IssueStatus issueStatus = issue.getIssueStatus();
//                        LocalDate planDueDate = issue.getPlanDueDate();
//                        LocalDate planActualDate = issue.getActualEndDate();
//                        return (issueStatus == IssueStatus.SOLVED || issueStatus == IssueStatus.CLOSED ||
//                                (planDueDate != null && planDueDate.isBefore(today)) ||
//                                (planActualDate != null && planActualDate.isBefore(today)));
//                    })
//                    .toList();
//
//            // Add filtered otherTasks for the current project to the list of otherTasks
//            otherTasks.addAll(filteredOtherTasks);
//            System.out.println("User..........Other Tasks" + otherTasks);
//
//        }
//
//        // Count the number of issues that must be done today
//        int todayIssuesCount = allIssues.size();
//        model.addAttribute("issues", allIssues);
//        model.addAttribute("todayIssuesCount", todayIssuesCount);
//
//
//        // Count the number of upcoming tasks
//        int upcomingIssuesCount = upcomingIssues.size();
//        model.addAttribute("upcomingIssuesCount", upcomingIssuesCount);
//        model.addAttribute("upcomingIssues", upcomingIssues);
//
//
//        // Count the number of other tasks
//        int otherTasksCount = otherTasks.size();
//        model.addAttribute("otherTasksCount", otherTasksCount);
//        model.addAttribute("otherTasks", otherTasks);
//
//
//
//        model.addAttribute("issue", new Issue());
//        model.addAttribute("project", new Project());
//
//        model.addAttribute("users", userService.getAllUsers());
//        model.addAttribute("issueStatuses", IssueStatus.values()); // Add statuses enum
//        model.addAttribute("priorities", Priority.values()); // Add priorities enum
//        model.addAttribute("issueTypes", issueTypeService.getAllIssueTypes());
//        model.addAttribute("categories", categoryService.getAllCategories());
//
//        Set<Project> projectList = projectService.getAllProjects();
//        model.addAttribute("projectList", projectList);
//
//
//        return "apps-tasks";
//    }
}
