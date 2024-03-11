package com.demo.project_management_system.controller;

import com.demo.project_management_system.dto.IssuePriorityUpdateRequest;
import com.demo.project_management_system.dto.IssueStatusUpdateRequest;
import com.demo.project_management_system.dto.UpdateIssueStatusRequest;
import com.demo.project_management_system.entity.*;
import com.demo.project_management_system.service.IssueService;
import com.demo.project_management_system.service.NotificationService;
import com.demo.project_management_system.service.ProjectService;
import com.demo.project_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IssueController {

    private final NotificationService notificationService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;


    @PostMapping(value = "/issue/process_issue")
    @ResponseBody
    public ResponseEntity<?> processIssue(@ModelAttribute("issue") Issue issue,
                                          @RequestParam("projectId") long projectId,
                                          @RequestParam("userIds") List<Long> userIds,
                                          @RequestParam(name = "attachments", required = false) List<MultipartFile> attachments) throws IOException {
        try {
            // Load the Project object based on the provided projectId
            Optional<Project> projectOptional = projectService.findById(projectId);

            if (projectOptional.isPresent()) {
                // Extract the Project object from Optional
                Project project = projectOptional.get();

                // Set the Project object on the Issue
                issue.setProject(project);

                // Retrieve User entities based on the provided user IDs
                Set<User> users = userService.findByIdIn(userIds);

                // Set the retrieved users on the project
                issue.setUsers(users);

                // Check if issue with same type and subject already exists in the project
                boolean issueExists = issueService.existsByProjectAndIssueTypeAndSubject(project, issue.getIssueType(), issue.getSubject());

                if (issueExists) {
                    String projectName = issue.getProject().getProjectName();
                    String projectLink = "<a href='/projects/" + projectId + "'>" + projectName + "</a>";
                    String errorMessage = "**An issue with the same Issue type and subject already exists in the same " + projectLink + " Project";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(errorMessage);
                }

                // Process attachments
                if (attachments != null && !attachments.isEmpty()) {
                    // Save the issue to get its ID
                    Issue savedIssue = issueService.save(issue);

                    // Create the directory using the savedIssue.getId()
                    String uploadDir = "issue-attachments/" + savedIssue.getId(); // Update upload directory
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    for (MultipartFile multipartFile : attachments) {
                        // Handle the file
                        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                        // Save the file to disk
                        Path filePath = uploadPath.resolve(fileName);
                        Files.write(filePath, multipartFile.getBytes());

                        // Save the file name to the list in the Issue entity
                        issue.getAttachmentFileNames().add(fileName);
                    }
                }

                issue.setStatus(1);

                // Save the issue (if not already saved)
                issueService.save(issue);

                notificationService.sendNotification(issue);

                // Process other form fields and perform necessary operations

                return ResponseEntity.ok("Issue created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Project not found for ID: " + projectId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create issue: " + e.getMessage());
        }
    }


    @GetMapping("/downloadAttachment")
    public ResponseEntity<Resource> downloadAttachment(@RequestParam("issueId") Long issueId, @RequestParam("fileName") String fileName) throws IOException {
        // Construct the path to the attachment file
        String attachmentDir = "issue-attachments"; // Directory where attachments are stored
        String filePath = attachmentDir + "/" + issueId + "/" + fileName;

        // Load the attachment file as a byte array
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

        // Create a ByteArrayResource to wrap the file content
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        // Build the response entity with the file content and appropriate headers
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }




    @PatchMapping("/updateIssueStatus")
    public ResponseEntity<String> updateIssueStatus(@RequestBody UpdateIssueStatusRequest request) {
        // Extract the numeric part of the issueId string
        int issueId = Integer.parseInt(request.getIssueId().replaceAll("[^\\d]", "")); // Removes all non-numeric characters
        String issueStatus = request.getNewIssueStatus();

        // Retrieve the issue from the database
        Optional<Issue> optionalIssue = issueService.findIssueById(issueId);
        if (optionalIssue.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Issue not found");
        }

        // Extract the issue object from Optional
        Issue issue = optionalIssue.get();

        // Update the issue status
        issue.setIssueStatus(IssueStatus.valueOf(request.getNewIssueStatus()));

        // Save the updated issue back to the database
        issueService.save(issue);

        return ResponseEntity.ok("Issue status updated successfully");
    }


    @DeleteMapping(value = "issue/deleteissue/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteIssue(@PathVariable int id) {
        Optional<Issue> issueOptional = issueService.findIssueById(id);
        if (issueOptional.isPresent()) {
            Issue issue = issueOptional.get();
            issue.setStatus(0); // Assuming 0 represents the deleted status
            issueService.save(issue);
            return ResponseEntity.ok().body("{\"message\": \"Issue has been deleted successfully\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Issue not found\"}");
        }
    }


    @GetMapping("/api/issue/{id}")
    @ResponseBody
    public ResponseEntity<Optional<Issue>> getIssueDetails(@PathVariable("id") int id) {
        Optional<Issue> issue = issueService.findIssueById(id);
        return ResponseEntity.ok(issue);
    }


    @PatchMapping("/issue/edit/{issueId}")
    public ResponseEntity<String> updateIssue(@PathVariable int issueId, @RequestBody Issue updatedIssue) {
        try {
            Optional<Issue> optionalExistingIssue = issueService.findIssueById(issueId);
            if (optionalExistingIssue.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Issue existingIssue = optionalExistingIssue.get();
            System.out.println("UPDATE ISSUE..." + updatedIssue);

            // Update properties of existingIssue
            existingIssue.setSubject(updatedIssue.getSubject());
            existingIssue.setDescription(updatedIssue.getDescription());
            existingIssue.setPriority(updatedIssue.getPriority());
            existingIssue.setPlanStartDate(updatedIssue.getPlanStartDate());
            existingIssue.setPlanDueDate(updatedIssue.getPlanDueDate());
//            existingIssue.setIssueType(updatedIssue.getIssueType());
            existingIssue.setCategory(updatedIssue.getCategory());

            System.out.println(updatedIssue.getPlanDueDate());

            // Update other fields as needed

            issueService.updateIssue(Optional.of(existingIssue));
            return ResponseEntity.ok("Issue updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating issue: " + e.getMessage());
        }
    }

    @GetMapping(value = "getIssueInfo")
    public String getIssueInfo(@RequestParam("issueId") int issueId, Model model) {
        Optional<Issue> optionalIssue = issueService.findIssueById(issueId);
        if (optionalIssue.isPresent()){
            Issue issue = optionalIssue.get();
            System.out.println("Issue More Info..." + issue);
            model.addAttribute("issue", issue);
            return "issueDetails";
        } else {
            return "issueNotFound";
        }
    }

    @GetMapping(value = "getIssueUserInfo")
    public String getIssueUserInfo(@RequestParam("issueId") int issueId, Model model) {
        Optional<Issue> optionalIssue = issueService.findIssueById(issueId);
        if (optionalIssue.isPresent()){
            Issue issue = optionalIssue.get();
            System.out.println("Issue User More Info..." + issue);
            model.addAttribute("issue", issue);
            return "issueUserDetails";
        } else {
            return "issueUserNotFound";
        }
    }

    @PostMapping("/api/removeUser")
    public ResponseEntity<String> removeUserFromIssue(@RequestParam Long userId, @RequestParam Long issueId) {
        try {
            // Retrieve the issue by its ID
            Optional<Issue> optionalIssue = issueService.findIssueById(issueId);
            if (optionalIssue.isEmpty()) {
                return ResponseEntity.notFound().build(); // Issue not found
            }

            Issue issue = optionalIssue.get();

            // Remove the user from the issue
            issue.getUsers().removeIf(user -> user.getId().equals(userId));

            // Save the updated issue
            issueService.updateIssue(Optional.of(issue));

            return ResponseEntity.ok("User removed from the issue successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing user from the issue: " + e.getMessage());
        }
    }


    @PostMapping("/issue/updatePriority")
    public ResponseEntity<String> updateIssuePriority(@RequestBody IssuePriorityUpdateRequest updatePriorityRequest) {
        // Extract data from update request
        long issueId = updatePriorityRequest.getIssueId();
        System.out.println("Project IssueId..." + issueId);

        String priority = updatePriorityRequest.getNewPriority();
        System.out.println("Issue Priority..." + priority);

        // Fetch issue from database by id
        Optional<Issue> optionalIssue = issueService.findById(issueId);
        if (optionalIssue.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Issue issue = optionalIssue.get();
        // Update issue with new data
        issue.setPriority(Priority.valueOf(priority));

        // Save updated issue
        issueService.save(issue);

        return ResponseEntity.ok("Issue Priority updated successfully");
    }


    @PostMapping("/issue/updateIssueStatus")
    public ResponseEntity<String> updateIssueStatus(@RequestBody IssueStatusUpdateRequest updateStatusRequest) {
        // Extract data from update request
        long issueId = updateStatusRequest.getIssueId();
        System.out.println("Project IssueId..." + issueId);

        String status = updateStatusRequest.getNewIssueStatus();
        System.out.println("Issue Status..." + status);

        // Fetch issue from database by id
        Optional<Issue> optionalIssue = issueService.findById(issueId);
        if (optionalIssue.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Issue issue = optionalIssue.get();
        // Update issue with new data
        issue.setIssueStatus(IssueStatus.valueOf(status));

        // Save updated issue
        issueService.save(issue);

        return ResponseEntity.ok("Issue Status updated successfully");
    }

}
