package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.*;
import com.demo.project_management_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ManagerController {
    @Autowired
    private UserService userService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueTypeService issueTypeService;

    @Autowired
    private CategoryService categoryService;

//    @GetMapping("/project-list")
//    public String showProject(HttpSession session , Model model){
//        // Retrieve the user object from the session
//        User loggedInUser = (User) session.getAttribute("loggedInUser");
//        // Pass the user object to the view
//        model.addAttribute("loggedInUser", loggedInUser);
//
//        model.addAttribute("project", new Project());
//
//        // Get the Authentication object from SecurityContextHolder
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        // Retrieve authorities from the Authentication object
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//
//
//        // Check if the logged-in user has ROLE_SYSTEM_ADMIN authority
//        boolean isProjectManager = authorities.stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROJECT_MANAGER"));
//
//
//        if (isProjectManager) {
//            // Get users with authority ROLE_MEMBER and status "Active"
//            List<User> members = userService.getUsersByAuthorityAndStatus("ROLE_MEMBER", "Active");
//
//            session.setAttribute("members", members);
//
//            model.addAttribute("members", members);
//
//            System.out.println("MMMMMMMMMMMMMMM " + members);
//
//            Set<Project> projectList = projectService.getProjectsByUsersId(loggedInUser.getId());
//            System.out.println(projectList);
//            model.addAttribute("projectList", projectList);
//            return "project-list";
//        }
//
//        return "project-list";
//    }

    @GetMapping("/tasks")
    public String viewTasks(HttpSession session , Model model){
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);

        long userId = loggedInUser.getId();
        System.out.println("loggedInUserId......... " + userId);

        // Find projects for the logged-in user
        Set<Project> userProjects = userService.findProjectsByUserId(userId);
        System.out.println("User Projects " + userProjects);

        // List to store all issues
        List<Issue> todayIssues = new ArrayList<>();
        List<Issue> upcomingIssues = new ArrayList<>();
        List<Issue> overdueTasks = new ArrayList<>();

        // Get today's date
        LocalDate today = LocalDate.now();
        System.out.println("TODAY DATE...." + today);

        // Map to store user ID and count of issues assigned to each user
        Map<Long, Long> userIssueCountMap = new HashMap<>();

        for (Project project : userProjects) {
            long projectId = project.getId();

            // Get issues for the current project
            Set<Issue> issuesForProject = projectService.getIssuesByProjectId(projectId);


            // Iterate through the issues and count the number of issues assigned to each user
            for (Issue issue : issuesForProject) {
                Set<User> assignedUsers = issue.getUsers();
                for (User user : assignedUsers) {
                    long assignedUserId = user.getId();
                    userIssueCountMap.put(assignedUserId, userIssueCountMap.getOrDefault(assignedUserId, Long.valueOf(0)) + 1);
                }
            }

            System.out.println("Issue Count Map " + userIssueCountMap);

            // Retrieve all issues from the database
            List<Issue> issues = issueService.findAll();

            // Add the list of issues to the model
            model.addAttribute("issues", issues);


        }

        // Filter the userIssueCountMap to get users with more than one issue
        List<User> usersWithMultipleIssues = userIssueCountMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> userService.getUserById(entry.getKey()))
                .collect(Collectors.toList());

        System.out.println("usersWithMultipleIssues..." + usersWithMultipleIssues);

        model.addAttribute("usersWithMultipleIssues", usersWithMultipleIssues);

        model.addAttribute("issue", new Issue());
        model.addAttribute("project", new Project());

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("issueStatuses", IssueStatus.values()); // Add statuses enum
        model.addAttribute("priorities", Priority.values()); // Add priorities enum
        model.addAttribute("issueTypes", issueTypeService.getAllIssueTypes());
        model.addAttribute("categories", categoryService.getAllCategories());

        Set<Project> projectList = projectService.getAllProjects();
        model.addAttribute("projectList", projectList);


        return "apps-tasks";
    }


}
