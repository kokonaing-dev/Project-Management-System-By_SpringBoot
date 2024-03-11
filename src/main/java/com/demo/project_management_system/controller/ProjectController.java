package com.demo.project_management_system.controller;

import com.demo.project_management_system.dto.CategoryData;
import com.demo.project_management_system.dto.IssueTypeData;
import com.demo.project_management_system.entity.*;
import com.demo.project_management_system.service.*;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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

import java.util.*;


@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final NotificationService notificationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private IssueService issueService;


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private IssueTypeService issueTypeService;

    @PostMapping(value = "/project/create")
    public ResponseEntity<?> createProject(@ModelAttribute("project") Project project,
                                           @RequestParam("userIds") List<Long> userIds,
                                           @RequestParam("loggedInUserId") int loggedInUserId,
                                           BindingResult result) {
        try {
            // Check if required fields are empty
            if (project.getProjectName() == null || project.getProjectName().isEmpty() ||
                    project.getProjectStartDate() == null || project.getProjectDueDate() == null) {
                return ResponseEntity.badRequest().body("Please fill in all required fields.");
            }
            // Check if project start date is greater than end date
            if (project.getProjectStartDate().isAfter(project.getProjectDueDate())) {
                return new ResponseEntity<>("Project start date cannot be after project end date", HttpStatus.BAD_REQUEST);
            }
            // Validate the project object
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Invalid data provided.");
            }

            // Check if the project name already exists
            boolean existingProject = projectService.isProjectNameExists(project.getProjectName());
            if (existingProject) {
                return ResponseEntity.badRequest().body("Project name already exists.");
            }

            System.out.println("Logged in User ID is Here " + loggedInUserId);
            // Retrieve the logged-in user from the userService
            User loggedInUser = userService.getUserById(loggedInUserId);

            // Retrieve User entities based on the provided user IDs
            Set<User> users = userService.findByIdIn(userIds);

            // Add the logged-in user to the set of users
            users.add(loggedInUser);

            // Set the retrieved users on the project
            project.setUsers(users);

            // Save the project
            Project savedProject = projectService.save(project);
            notificationService.sendNotification(project);

            return ResponseEntity.ok(savedProject);
        } catch (Exception e) {
            // Handle exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create project");
        }
    }

    @DeleteMapping("/deleteProject/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable long projectId) {
        try {
            // Delete the project by its ID
            projectService.deleteProjectById(projectId);
            return ResponseEntity.ok().body("Project deleted successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete project");
        }
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

    @GetMapping("/projectDetail/{projectId}")
    public String goToHomePage(@PathVariable long projectId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model, HttpSession session) {
        User user = userService.findUserByEmail(userDetails.getUsername());
        // Store the user object in the session
        session.setAttribute("loggedInUser", user);

        // Expose loggedInUser as a model attribute
        model.addAttribute("loggedInUser", user);

        Optional<Project> optionalProject = Optional.ofNullable(projectService.getProjectById(projectId));
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            // Fetch project status using your service method
            String projectStatus = project.calculateProjectStatus(); // Assuming you have a method to calculate project status
            model.addAttribute("projectStatus", projectStatus); // Add project status to the model

            int inProgressCount = issueService.getIssuesCountByStatusAndProjectId(IssueStatus.IN_PROGRESS, projectId);
            int openCount = issueService.getIssuesCountByStatusAndProjectId(IssueStatus.OPEN, projectId);
            int solvedCount = issueService.getIssuesCountByStatusAndProjectId(IssueStatus.SOLVED, projectId);
            int closedCount = issueService.getIssuesCountByStatusAndProjectId(IssueStatus.CLOSED, projectId);
            int pendingCount = issueService.getIssuesCountByStatusAndProjectId(IssueStatus.PENDING, projectId);


            int totalIssues = issueService.getTotalIssuesByProjectId(projectId);
            int totalAssignedUsers = userService.getTotalAssignedUsersByProjectId(projectId);

            model.addAttribute("project", project);
            model.addAttribute("totalIssues", totalIssues);
            model.addAttribute("totalAssignedUsers", totalAssignedUsers);
            model.addAttribute("inProgressCount", inProgressCount);
            model.addAttribute("openCount", openCount);
            model.addAttribute("solvedCount", solvedCount);
            model.addAttribute("closedCount", closedCount);
            model.addAttribute("pendingCount", pendingCount);


            // Fetch issue types data
            List<IssueType> issueTypes = issueTypeService.getAllIssueTypes(); // Assuming you have a service method to fetch all issue types

            // Fetch mapping of issue types to statuses
            Map<IssueType, Set<IssueStatus>> issueTypeStatusMapping = issueTypeService.getIssueTypeStatusMapping(); // Assuming you have a service method to fetch issue type to status mapping

            // Create a map to store issue types counts by status
            Map<IssueStatus, Map<String, Integer>> issueTypeDataByStatus = new HashMap<>();

            // Populate issue type data by status
            for (IssueType issueType : issueTypes) {
                Set<IssueStatus> statuses = issueTypeStatusMapping.getOrDefault(issueType, Collections.emptySet());
                for (IssueStatus status : statuses) {
                    issueTypeDataByStatus.putIfAbsent(status, new HashMap<>());
                    issueTypeDataByStatus.get(status).put(issueType.getIssueName(), issueService.getIssueCountByTypeAndStatus(issueType.getId(), status)); // Assuming you have a service method to get issue count by type and status
                }
            }
            // Add issue type data to model
            model.addAttribute("issueTypeDataByStatus", issueTypeDataByStatus);
            // Add logic to handle the request with the project id

            // Fetch categories data
            List<Category> categories = categoryService.getAllCategories(); // Assuming you have a service method to fetch all categories

            // Fetch mapping of issue types to statuses
            Map<Category, Set<IssueStatus>> categoryStatusMapping = categoryService.getCategoryStatusMapping();

            // Create a map to store category data by status
            Map<IssueStatus, Map<String, Integer>> categoryDataByStatus = new HashMap<>();

            // Populate category data by status
            for (Category category : categories) {
                Set<IssueStatus> statuses = categoryStatusMapping.getOrDefault(category, Collections.emptySet());
                for (IssueStatus status : statuses) {
                    categoryDataByStatus.putIfAbsent(status, new HashMap<>());
                    categoryDataByStatus.get(status).put(category.getCategoryName(), issueService.getIssueCountByCategoryAndStatus(category.getId(), status)); // Assuming you have a service method to get issue count by category and status
                }
            }

            // Add category data to model
            model.addAttribute("categoryDataByStatus", categoryDataByStatus);


        } else {
            // Handle project not found
        }
        return "project-detail"; // return the name of your homepage template
    }

    @PostMapping("/editProject")
    public String updateProject(@ModelAttribute("project") Project project) {
        // Update the project details using the project service
        projectService.updateProject(project);
        // Redirect to a suitable page after updating the project
        return "redirect:/projectDetail/" + project.getId(); // Redirect to the project detail page
    }

    @GetMapping("/api/issueTypeData")
    @ResponseBody
    public List<IssueTypeData> getIssueTypeData() {
        List<Object[]> rawData = issueService.getIssueTypeData(); // Assuming you have a method to fetch issue type data
        List<IssueTypeData> issueTypeData = new ArrayList<>();
        for (Object[] row : rawData) {
            String name = (String) row[0];
            Long count = (Long) row[1];
            issueTypeData.add(new IssueTypeData(name, count));
        }
        return issueTypeData;
    }

    @GetMapping("/api/categoryData")
    @ResponseBody
    public List<CategoryData> getCategoryData() {
        List<Object[]> rawData = issueService.getCategoryData(); // Assuming you have a method to fetch issue type data
        List<CategoryData> categoryData = new ArrayList<>();
        for (Object[] row : rawData) {
            String name = (String) row[0];
            Long count = (Long) row[1];
            categoryData.add(new CategoryData(name, count));
        }
        return categoryData;
    }


}
