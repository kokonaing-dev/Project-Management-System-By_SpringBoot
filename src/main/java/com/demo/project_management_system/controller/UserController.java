package com.demo.project_management_system.controller;

import com.demo.project_management_system.File.FileUploadUtil;
import com.demo.project_management_system.dto.UserIssueRequest;
import com.demo.project_management_system.entity.*;
import com.demo.project_management_system.repository.RoleRepository;
import com.demo.project_management_system.service.IssueService;
import com.demo.project_management_system.service.ProjectService;
import com.demo.project_management_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.*;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private RoleRepository roleRepo;

    @PostMapping("/process_register")
    public String processRegistration( @RequestParam("role") String role,@Valid User user,
                                       @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                                       BindingResult result, Model model) throws IOException {
        if (result.hasErrors()) {
            // If there are validation errors, return the registration form with error messages
            return "auth-register";
        }

        if (role.isEmpty()){
            role="ROLE_MEMBER";
        }

        // Check if the username or email already exists
        if (userService.isUserExists(user.getUsername(), user.getEmail())) {
            model.addAttribute("usernameEmailError", "Username or email already exists");
            return "auth-register";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
//        System.out.println(encodedPassword);

        // Fetch the existing Role object for ROLE_MEMBER
        Optional<Role> optionalMemberRole = roleRepo.findByAuthority(role);

        // Use the existing role if found
        // Create the role if not found (optional)
        String finalRole = role;
        Role memberRole = optionalMemberRole.orElseGet(() -> roleRepo.save(new Role(finalRole)));

        HashSet<Role> roles = new HashSet<>();
        roles.add(memberRole);

        // Set the password and add the role to the user's authorities
        user.setPassword(encodedPassword);
        user.setStatus("Active");
        user.setAuthorities(roles);

//        User savedUser = userService.saveUser(user);
        String uploadDir;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            uploadDir = "user-photos/" + savedUser.getId(); // Update upload directory
            FileUploadUtil.saveFile(uploadDir, fileName, (InputStream) multipartFile);
        } else {
            // If no photo is uploaded, set a default photo
            String defaultPhotoFileName = "user-profile.jpg"; // Change "user-profile.jpg" to the filename of your default photo
            user.setPhotos(defaultPhotoFileName);
            User savedUser = userService.save(user);

            uploadDir = "user-photos/" + savedUser.getId(); // Update upload directory
            // Create directory if it doesn't exist
            Files.createDirectories(Paths.get(uploadDir));

            // Save default photo using FileUploadUtil
            ClassPathResource classPathResource = new ClassPathResource("static/" + defaultPhotoFileName);
            InputStream inputStream = classPathResource.getInputStream();
            FileUploadUtil.saveFile(uploadDir, defaultPhotoFileName, inputStream);
        }

        return "redirect:/?successMessage=Registration+successful";
    }

    @PostMapping(value = "/user/save/{id}")
    public String saveProfile(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session, @PathVariable("id") int id,
                              @RequestParam("image") MultipartFile multipartFile) throws IOException {
        User loggedInUser = userService.findUserByEmail(userDetails.getUsername());
        // Store the user object in the session
        session.setAttribute("loggedInUser", loggedInUser);

        // Expose loggedInUser as a model attribute
        model.addAttribute("loggedInUser", loggedInUser);

        // Retrieve the user by id
        Optional<User> optionalUser = userService.findById(id);

        // If the user exists and a new image is uploaded
        if (optionalUser.isPresent() && !multipartFile.isEmpty()) {
            User user = optionalUser.get(); // Extract the actual User object from Optional

            // Delete the existing photo file if it exists
            if (user.getPhotos() != null) {
                FileUploadUtil.deleteFile("user-photos/" + user.getId(), user.getPhotos());
            }

            // Set the new photo
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);

            // Save the updated user
            userService.save(user);

            // Save the new photo file
            String uploadDir = "user-photos/" + user.getId();
            FileUploadUtil.saveUpdateFile(uploadDir, fileName, multipartFile);
        }

        return "redirect:/pages-profile";
    }


    @PostMapping(value = "/updateProfile/{userId}")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session,
                                @RequestParam("username") String username,
                                @RequestParam("email") String email,
                                @RequestParam("userId") int userId) {
        User loggedInUser = userService.findUserByEmail(userDetails.getUsername());
        // Store the user object in the session
        session.setAttribute("loggedInUser", loggedInUser);

        // Expose loggedInUser as a model attribute
        model.addAttribute("loggedInUser", loggedInUser);
        // Find the user by userId
        Optional<User> optionalUser = userService.findById(userId);

        if (optionalUser.isEmpty()) {
            // Handle case where user is not found
            return "redirect:/pages-profile"; // For example, return a view to display an error message
        }

        User user = optionalUser.get();

        // Here you have the user object, you can update its properties
        user.setUsername(username);
        user.setEmail(email);

        // Update the user in the database
        userService.save(user);

        return "redirect:/pages-profile";

    }



    @PostMapping(value = "/updatePassword/{id}")
    public String updatePassword(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session,
                                 @PathVariable int id,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("re_typeNewPassword") String re_typeNewPassword) {
        User loggedInUser = userService.findUserByEmail(userDetails.getUsername());
        // Store the user object in the session
        session.setAttribute("loggedInUser", loggedInUser);

        // Expose loggedInUser as a model attribute
        model.addAttribute("loggedInUser", loggedInUser);

        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Check if the current password matches the stored password
            if (!encoder.matches(currentPassword, user.getPassword())) {
                model.addAttribute("error", "Current password is incorrect. Please try again.");
                return "redirect:/updatePassword/{id}";
            }

            // Check if the new passwords match
            if (!newPassword.equals(re_typeNewPassword)) {
                model.addAttribute("error", "New passwords do not match. Please try again.");
                return "redirect:/updatePassword/{id}";
            }

            // Update the password
            String encodedPassword = encoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userService.save(user);

            model.addAttribute("successMessage", "Password updated successfully!");
            return "redirect:/pages-profile";
        }

        // Handle the case where the user is not found
        return "redirect:/pages-profile";
    }

    @GetMapping("/user/fetch_users")
    @ResponseBody
    public List<User> fetchUsersByProjectId(@RequestParam int projectId) {
        System.out.println("User IDs associated with PJ ID "+ projectId);
        return userService.getUsersByProjectId(projectId);
    }


    //For add people in Issue
    @GetMapping("/api/users/{issueId}")
    @ResponseBody
    public List<User> fetchUsersNotInIssue(@PathVariable int issueId) {
        System.out.println("Users not included in Issue ID " + issueId);

        Issue issue = issueService.findById(issueId).orElse(null);
        System.out.println("Issue: " + issue);

        List<User> usersNotInIssue = new ArrayList<>();

        if (issue != null) {
            Project project = issue.getProject();
            System.out.println("Project associated with the Issue: " + project);

            if (project != null) {
                long projectId = project.getId();
                System.out.println("Project ID of Issue: " + projectId);

                // Retrieve all users associated with the project
                List<User> allUsersInProject = userService.getUsersByProjectId(projectId);

                // Retrieve users associated with the issue
                Set<User> usersInIssue = issue.getUsers();
                System.out.println("Users In Issue: " + usersInIssue);

                // Filter users who are not in the issue
                for (User user : allUsersInProject) {
                    if (!usersInIssue.contains(user)) {
                        usersNotInIssue.add(user);
                    }
                }

                // Remove users already in the issue from the list
                for (User user : usersInIssue) {
                    usersNotInIssue.remove(user);
                }

                // Print user details to console
                for (User user : usersNotInIssue) {
                    System.out.println("User ID: " + user.getId() + ", Name: " + user.getUsername() + ", Email: " + user.getEmail());
                }
            } else {
                System.out.println("No project associated with the issue.");
            }
        } else {
            System.out.println("Issue not found with ID: " + issueId);
        }

        return usersNotInIssue;
    }


    @PostMapping("/api/user_issue/add")
    public ResponseEntity<String> addUserToIssue(@RequestBody UserIssueRequest request) {
        // Get issueId and selectedUserIds from the request
        long issueId = request.getIssueId();
        List<Long> selectedUserIds = request.getSelectedUsers();

        System.out.println("SELECTED ISSUE ID...." + issueId);
        System.out.println("SELECTED USER...." + selectedUserIds);

        // Retrieve the Issue entity based on the issueId
        Issue issue = issueService.findById(issueId).orElse(null);
        if (issue == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Issue not found");
        }

        // Retrieve User entities based on the provided user IDs
        Set<User> users = userService.findByIdIn(selectedUserIds);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found with the provided IDs");
        }

        // Associate the users with the issue
        issue.getUsers().addAll(users);

        // Save the updated issue to persist the association
        issueService.save(issue);

        return ResponseEntity.ok("Users added to the issue successfully.");
    }


    @GetMapping(value = "/apps-todo")
    public String showAppsTodo(){ return "apps-todo"; }

    @GetMapping(value="/profile")
    public String showUserProfile() {
    	return "profile";
    }

    @GetMapping(value = "/usermanage")
    public String showUserList(){
        return "usermanage";
    }


    @GetMapping(value = "/charts-apex-bar")
    public String chartBar(){
        return "charts-apex-bar";
    }

    @GetMapping(value = "charts-apex-pie")
    public String chartPie(){
        return "charts-apex-pie";
    }

}
