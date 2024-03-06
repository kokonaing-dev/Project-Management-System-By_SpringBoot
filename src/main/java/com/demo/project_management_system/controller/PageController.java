package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.*;
import com.demo.project_management_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private IssueTypeService issueTypeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/dashboard")
    public String gettingStart(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session) {
        User user = userService.findUserByEmail(userDetails.getUsername());
        // Store the user object in the session
        session.setAttribute("loggedInUser", user);

        // Expose loggedInUser as a model attribute
        model.addAttribute("loggedInUser", user);
        model.addAttribute("project", new Project());

        model.addAttribute("issue", new Issue());
        model.addAttribute("project", new Project());

        List<User> users = userService.getAllActiveUsers();
        model.addAttribute("usersList", users);

        model.addAttribute("issueStatuses", IssueStatus.values()); // Add statuses enum
        model.addAttribute("priorities", Priority.values()); // Add priorities enum

        model.addAttribute("issueTypes", issueTypeService.getAllIssueTypes());
        model.addAttribute("categories", categoryService.getAllCategories());

        Set<Project> projectList = projectService.getAllProjects();
        model.addAttribute("projectList", projectList);

        // Get the Authentication object from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Retrieve authorities from the Authentication object
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Check if the logged-in user has ROLE_SYSTEM_ADMIN authority
        boolean isSystemAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

        boolean isProjectManager = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROJECT_MANAGER"));

        // Check if the logged-in user has ROLE_PROJECT_MANAGER or ROLE_MEMBER authority
        boolean isProjectManagerOrMember = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROJECT_MANAGER") || auth.getAuthority().equals("ROLE_MEMBER"));

        List<Issue> issues;
        if (isSystemAdmin){
            // Convert the Set to a List for the admin user
            issues = new ArrayList<>(issueService.getAllIssues());
        } else if (isProjectManagerOrMember) {
            // Fetch issues by userId for non-admin users
            issues = issueService.getIssuesByUserId(user.getId());
        }else {
            // Default to an empty list of issues for other users
            issues = new ArrayList<>();
        }
        model.addAttribute("issues", issues);


        if (isSystemAdmin) {
            // Get all projects
            List<Project> projects = projectService.getAllProjectsWithCounts();
            model.addAttribute("projects", projects);
        } else if (isProjectManagerOrMember) {
            // Get projects by user ID
            Set<Project> projects = projectService.getProjectsByUserId(user.getId());
            model.addAttribute("projects", projects);
        }
        if(isSystemAdmin){
            // Get users with authorities POLE_PROJECT_MANGER and role member
            List<User> projectManagerAndMembers = userService.getUsersByAuthorities("ROLE_PROJECT_MANAGER","ROLE_MEMBER");
            session.setAttribute("projectManagersAndMembers", projectManagerAndMembers);
            model.addAttribute("projectManagersAndMembers", projectManagerAndMembers);
            return "dashboard";
        }
        if (isProjectManager) {
            // Get users with authority ROLE_MEMBER
            List<User> members = userService.getUsersByAuthority("ROLE_MEMBER");
            session.setAttribute("members", members);
            model.addAttribute("members", members);

//            System.out.println("MMMMMMMMMMMMMMM " + members);
//            return "project-list";
        }

        return "dashboard";
    }


    @GetMapping("/")
    public String authSignIn(Model model){
        return "auth-login";
    }

    @GetMapping("/register")
    public String authSignUp(ModelMap modelMap){
        modelMap.addAttribute("user", new User());
        return "auth-register";
    }

    @GetMapping("/pages-profile")
    public String pagesProfile(HttpSession session, Model model){
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        long userId = loggedInUser.getId();
        System.out.println("User IDDDDDD " + userId);

        // Find projects for the logged-in user
        Set<Project> userProjects = userService.findProjectsByUserId(userId);
        System.out.println("User Projects " + userProjects);

        // Fetch the number of issues for each project
        for (Project project : userProjects) {
            int numberOfIssues = projectService.findNumberOfIssuesByProjectId(project.getId());
            System.out.println("Number Of Issues.........is " + numberOfIssues);
            model.addAttribute("numberOfIssues", numberOfIssues);
            project.setNumberOfIssues(numberOfIssues);
        }

        model.addAttribute("projectList", userProjects);


        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);

        System.out.println("Logged In User ID " + loggedInUser.getId());

        return "pages-profile";
    }

    @GetMapping("/auth-logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:";
    }

    @GetMapping("/admins/role")
    public String rolePage(){
        return "users-role-list";
    }

    @GetMapping("/board")
    public String viewBoards(Model model,HttpSession session){
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);


        Set<Project> projectList = projectService.getProjectsByUserId(loggedInUser.getId());
        model.addAttribute("projectList", projectList);

        Set<Issue> issueList = new HashSet<>();
        for (Project project : projectList) {
            issueList.addAll(issueService.findIssueByProjectId(project.getId()));
        }


        Set<User> userList = new HashSet<>();
        for (Issue issue : issueList) {
            userList.addAll(issue.getUsers());
        }
        System.out.println("USER LIST ...." + userList);


        // Filter issues by status
        List<Issue> todoIssues = issueList.stream()
                .filter(issue -> issue.getIssueStatus() == IssueStatus.OPEN && issue.getStatus() == 1)
                .collect(Collectors.toList());

        List<Issue> inProgressIssues = issueList.stream()
                .filter(issue -> issue.getIssueStatus() == IssueStatus.IN_PROGRESS && issue.getStatus() == 1)
                .collect(Collectors.toList());

        List<Issue> solvedIssues = issueList.stream()
                .filter(issue -> issue.getIssueStatus() == IssueStatus.SOLVED && issue.getStatus() == 1)
                .collect(Collectors.toList());

        List<Issue> pendingIssues = issueList.stream()
                .filter(issue -> issue.getIssueStatus() == IssueStatus.PENDING && issue.getStatus() == 1)
                .collect(Collectors.toList());

        List<Issue> closedIssues = issueList.stream()
                .filter(issue -> issue.getIssueStatus() == IssueStatus.CLOSED && issue.getStatus() == 1)
                .collect(Collectors.toList());


        // Add filtered lists to the model
        model.addAttribute("todoIssues", todoIssues);
        model.addAttribute("inProgressIssues", inProgressIssues);
        model.addAttribute("solvedIssues", solvedIssues);
        model.addAttribute("pendingIssues", pendingIssues);
        model.addAttribute("closedIssues", closedIssues);


        model.addAttribute("issue", new Issue());
        model.addAttribute("project", new Project());

        List<IssueType> issueTypeList = issueTypeService.getAllIssueTypes();
        model.addAttribute("issueTypeList", issueTypeList);

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("issueStatuses", IssueStatus.values()); // Add statuses enum
        model.addAttribute("priorities", Priority.values()); // Add priorities enum
        model.addAttribute("issueTypes", issueTypeService.getAllIssueTypes());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "apps-kanban";
    }

    @GetMapping("/auth-recoverpw")
    public String recoverPw(){ return "auth-recoverpw"; }


    @GetMapping("/project-create")
    public String projectCreate(){
        return "project-create";
    }


    @GetMapping("/tasks-detail")
    public String viewTasksDetail(){
        return "apps-tasks-details";
    }


    @GetMapping("/apps-chat")
    public String chatTest(HttpSession session, Model model){
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);
        return "apps-chat";
    }


}
