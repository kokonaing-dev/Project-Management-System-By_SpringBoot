package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.*;
import com.demo.project_management_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller

public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueTypeService issueTypeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RoleService roleService;



    @GetMapping("/userList")
    public String viewAllUser(Model model , HttpSession session) {
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);

        // Retrieve all users from the database
        List<User> userList = userService.getAllActiveUsers();

        // Filter out the logged-in user from the user list
        userList = userList.stream()
                .filter(user -> !user.getId().equals(loggedInUser.getId())) // Assuming user IDs are used for identification
                .filter(user -> user.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_SYSTEM_ADMIN"))) // Filter out users with ROLE_SYSTEM_ADMIN authority
                .collect(Collectors.toList());
        model.addAttribute("userList", userList);
        return "users-list";
    }

    @GetMapping("/userDetail/{userId}")
    public String userDetailPage(@PathVariable int userId, Model model,HttpSession session){
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);

        // Retrieve the user by ID
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        // Retrieve the active projects associated with the user
        Set<Project> activeProjects = userService.getActiveProjectsByUser(user);

        // Calculate project status for each active project
        for (Project project : activeProjects) {
            String projectStatus = project.calculateProjectStatus();
            project.setStatus(projectStatus);
        }

        // Add the active projects to the model
        model.addAttribute("userProjects", activeProjects);

        // Fetch the available roles from the database
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);

        return "user-profile";
    }

    @PostMapping("/updateProfile")
    @ResponseBody
    public ResponseEntity<String> updateProfile(@RequestBody User user) {
        // Update user profile in the database using userService
        userService.updateUser(user);
        // Return a success response
        return ResponseEntity.ok().body("Profile updated successfully");
    }

    @DeleteMapping(value = "/user/deleteUser/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setStatus("Deactivated");
            String randomPassword = generateRandomPassword(8);
            user.setPassword(randomPassword);
            userService.save(user);
            return ResponseEntity.ok().body("{\"message\": \"User has been deleted successfully\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"User not found\"}");
        }
    }

    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    @GetMapping("/project-list")
    public String showProject(HttpSession session , Model model){
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);

        model.addAttribute("project", new Project());

        // Get the Authentication object from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Retrieve authorities from the Authentication object
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Check if the logged-in user has ROLE_SYSTEM_ADMIN authority
        boolean isSystemAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
        // Check if the logged-in user has ROLE_SYSTEM_ADMIN authority
        boolean isProjectManager = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROJECT_MANAGER"));

        if (isSystemAdmin || isProjectManager) {
            // Get all projects
            List<Project> projects = projectService.getAllProjectsWithCounts();
            model.addAttribute("projects", projects);
        }

        if (isSystemAdmin) {
            // Get users with authorities ROLE_PROJECT_MANAGER and ROLE_MEMBER
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_PROJECT_MANAGER");
            roles.add("ROLE_MEMBER");
            List<User> projectManagersAndMembers = userService.getUsersByAuthoritiesAndStatus(roles, "Active");

            session.setAttribute("projectManagersAndMembers", projectManagersAndMembers);

            model.addAttribute("projectManagersAndMembers", projectManagersAndMembers);

            System.out.println("PPPPPPPPMMMMMMMMMM " +projectManagersAndMembers);
            return "project-list";
        }
        if (isProjectManager) {
            // Get users with authority ROLE_MEMBER
            List<User> members = userService.getUsersByAuthority("ROLE_MEMBER");

            session.setAttribute("members", members);

            model.addAttribute("members", members);

            System.out.println("MMMMMMMMMMMMMMM " + members);

        }

        return "project-list";
    }

    @GetMapping("/project-detail")
    public String projectDeatil(HttpSession session , Model model){
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);
        return "project-detail";
    }

    @GetMapping("/admin/tasks-detail")
    public String viewTasksDetail(){
        return "apps-tasks-details";
    }

    @GetMapping("/setting")
    public String viewSetting(Model model, HttpSession session){
        // Retrieve the user object from the session
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Pass the user object to the view
        model.addAttribute("loggedInUser", loggedInUser);

        List<IssueType> issueTypes = issueTypeService.getAllIssueTypes();
        model.addAttribute("issueTypes", issueTypes);
        model.addAttribute("issueType", new IssueType());

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories",categories);
        model.addAttribute("category", new Category());


        return "setting";
    }

}
