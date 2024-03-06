package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.entity.User;
import com.demo.project_management_system.service.IssueService;
import com.demo.project_management_system.service.ProjectService;
import com.demo.project_management_system.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private IssueService issueService;

    @PostMapping(value = "/project/create")
    public ResponseEntity<?> createProject(@ModelAttribute("project") Project project, @RequestParam("userIds") List<Long> userIds, @RequestParam("loggedInUserId") int loggedInUserId, Model model) {
        // Check if projectName already exists
        if (projectService.isProjectNameExists(project.getProjectName())) {
            // Handle case where projectName already exists
            return new ResponseEntity<>("Project with the same name already exists", HttpStatus.BAD_REQUEST);
        }
        // Check if project start date is greater than end date
        if (project.getProjectStartDate().isAfter(project.getProjectEndDate())) {
            return new ResponseEntity<>("Project start date cannot be after project end date", HttpStatus.BAD_REQUEST);
        }

        System.out.println("Logged in User ID is Here " + loggedInUserId);
        // Retrieve the logged-in user from the userService
        User loggedInUser = userService.getUserById(loggedInUserId);

        // Retrieve User entities based on the provided user IDs
        Set<User> users = userService.findByIdIn(userIds);

        // Add the logged-in user to the set of users
        users.add(loggedInUser);

        System.out.println("SAVEDDDDDDDDDDDDD");

        // Set the retrieved users on the project
        project.setUsers(users);

        Project savedProject = projectService.save(project);
        return new ResponseEntity<>(savedProject, HttpStatus.OK);
    }

    @GetMapping("/projects/delete/{projectId}")
    public String deleteProject(@PathVariable int projectId) {
        try {
            projectService.deleteProjectById(projectId);
            return "Project with ID " + projectId + " has been deleted successfully";
        } catch (Exception e) {
            return "Error deleting project with ID " + projectId + ": " + e.getMessage();
        }
    }

    @GetMapping("/projects/update/{projectId}")
    public ResponseEntity<Project> getProjectDetailsForUpdate(@PathVariable int projectId) {
        // Fetch the project details from the database based on the project ID
        Project project = projectService.getProjectById(projectId);

        if (project != null) {
            return ResponseEntity.ok().body(project);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/homepage/{projectId}")
    public String goToHomePage(@PathVariable int projectId, @AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session) {
        User user = userService.findUserByEmail(userDetails.getUsername());
        // Store the user object in the session
        session.setAttribute("loggedInUser", user);

        // Expose loggedInUser as a model attribute
        model.addAttribute("loggedInUser", user);

        Optional<Project> optionalProject = Optional.ofNullable(projectService.getProjectById(projectId));
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            int totalIssues = issueService.getTotalIssuesByProjectId((long) projectId);
            int totalAssignedUsers = userService.getTotalAssignedUsersByProjectId((long) projectId);
            model.addAttribute("project", project);
            model.addAttribute("totalIssues", totalIssues);
            model.addAttribute("totalAssignedUsers", totalAssignedUsers);
        } else {
            // Handle project not found
        }

        /* Add logic to handle the request with the project id */
        return "project-detail"; // return the name of your homepage template
    }
}
