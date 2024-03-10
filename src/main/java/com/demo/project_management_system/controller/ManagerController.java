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
        List<Issue> closedIssues = new ArrayList<>();

        // Get today's date
        LocalDate today = LocalDate.now();
        System.out.println("TODAY DATE...." + today);


        for (Project project : userProjects) {
            Long projectId = project.getId();

            // Get issues for the current project
            Set<Issue> issuesForProject = projectService.getIssuesByProjectId(projectId);
            System.out.println("Issues For Project " + issuesForProject);

            // Filter issues where planStartDate is equal to today's date
            List<Issue> filteredTodayIssues = issuesForProject.stream()
                    .filter(issue -> {
                        IssueStatus issueStatus = issue.getIssueStatus();
                        LocalDate planStartDate = issue.getPlanStartDate();

                        return (planStartDate.equals(today));

//                        return !(issueStatus == IssueStatus.IN_PROGRESS ||
//                                issueStatus == IssueStatus.SOLVED ||
//                                issueStatus == IssueStatus.PENDING ||
//                                issueStatus == IssueStatus.CLOSED) &&
//                                ((planStartDate == null || planStartDate.equals(today)));
                    })
                    .toList();

            // Add filtered issues for today to the list of all issues
            todayIssues.addAll(filteredTodayIssues);
            model.addAttribute("todayIssues", todayIssues);
            System.out.println("User..........Today Issues" + todayIssues);

            // Count the number of issues that must be done today
            int todayIssuesCount = todayIssues.size();
            model.addAttribute("todayIssuesCount", todayIssuesCount);


            //filteredUpcomingIssues
            List<Issue> filteredUpcomingIssues = issuesForProject.stream()
                    .filter(issue -> {
                        IssueStatus issueStatus = issue.getIssueStatus();
                        LocalDate planStartDate = issue.getPlanStartDate();

                        return (planStartDate.isAfter(today));

//                        return !(issueStatus == IssueStatus.IN_PROGRESS ||
//                                issueStatus == IssueStatus.SOLVED ||
//                                issueStatus == IssueStatus.PENDING ||
//                                issueStatus == IssueStatus.CLOSED) &&
//                                ((planStartDate == null || planStartDate.isAfter(today)));
                    })
                    .toList();

            // Add filtered upcoming issues
            upcomingIssues.addAll(filteredUpcomingIssues);
            model.addAttribute("upcomingIssues", upcomingIssues);
            System.out.println("User..........Upcoming Issues" + upcomingIssues);

            // Count the number of upcoming tasks
            int upcomingIssuesCount = upcomingIssues.size();
            model.addAttribute("upcomingIssuesCount", upcomingIssuesCount);
            model.addAttribute("upcomingIssues", upcomingIssues);



            // Filter issues for overdueTasks
            List<Issue> filteredOverTasks = issuesForProject.stream()
                    .filter(issue -> {
                        IssueStatus issueStatus = issue.getIssueStatus();
                        LocalDate planDueDate = issue.getPlanDueDate();

                        return (planDueDate.isBefore(today));
                    })
                    .toList();

            // Add filtered overdueTasks for the current project to the list of overdueTasks
            overdueTasks.addAll(filteredOverTasks);
            model.addAttribute("overdueTasks", overdueTasks);
            System.out.println("User..........overdue Tasks" + overdueTasks);

            // Count the number of overdue tasks
            int overdueTasksCount = overdueTasks.size();
            model.addAttribute("overdueTasksCount", overdueTasksCount);




            // Filter issues for CLOSED state
            List<Issue> filteredClosedIssues = issuesForProject.stream()
                    .filter(issue -> issue.getIssueStatus() == IssueStatus.CLOSED)
                    .toList();

            // Add filtered closed issues
            closedIssues.addAll(filteredClosedIssues);
            model.addAttribute("closedIssues", closedIssues);
            System.out.println("User..........Closed Issues" + closedIssues);

            // Count the number of closed issues
            int closedIssuesCount = closedIssues.size();
            model.addAttribute("closedIssuesCount", closedIssuesCount);

        }



        Set<Issue> issues = issueService.getAllIssues();
        // Map to store user IDs and their corresponding issue counts
        Map<Long, Integer> userIssueCountMap = new HashMap<>();

        // Count issues for each user
        for (Issue issue : issues) {
            Set<User> assignees = issue.getUsers(); // Assuming there's a method to get the assignee users
            for (User assignee : assignees) {
                long assigneeId = assignee.getId();
                userIssueCountMap.put(assigneeId, userIssueCountMap.getOrDefault(assigneeId, 0) + 1);
            }
        }

        // Debugging output: Print user ID and corresponding issue count
        System.out.println("User Issue Counts: " + userIssueCountMap);

        // Filter users with more than one issue
        List<User> usersWithMultipleIssues = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : userIssueCountMap.entrySet()) {
            if (entry.getValue() >= 2) { // Check if user has 2 or more issues
                User user = userService.findUserById(entry.getKey()); // Assuming there's a method to find a user by ID
                if (user != null) {
                    usersWithMultipleIssues.add(user);
                }
            }
        }
        model.addAttribute("usersWithMultipleIssues", usersWithMultipleIssues);
        // Now you have the list of users who have two or more issues
        System.out.println("Users with two or more issues: " + usersWithMultipleIssues);

        int totalUsersWithMultipleIssues = usersWithMultipleIssues.size();
        model.addAttribute("totalUsersWithMultipleIssues", totalUsersWithMultipleIssues);



        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("issueStatuses", IssueStatus.values()); // Add statuses enum
        model.addAttribute("priorities", Priority.values()); // Add priorities enum
        model.addAttribute("issueTypes", issueTypeService.getAllIssueTypes());
        model.addAttribute("categories", categoryService.getAllCategories());

        Set<Project> projectList = projectService.getAllProjects();
        model.addAttribute("projectList", projectList);

        model.addAttribute("issue", new Issue());
        model.addAttribute("project", new Project());

        return "apps-tasks";
    }


}
