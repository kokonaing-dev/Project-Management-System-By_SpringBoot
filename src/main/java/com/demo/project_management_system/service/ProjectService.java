package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.Issue;
import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.entity.User;
import com.demo.project_management_system.repository.IssueRepository;
import com.demo.project_management_system.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;

    public Set<Project> getAllProjects() {
        List<Project> projectList = projectRepository.findAll();
        return new HashSet<>(projectList);
    }

    // Method to check if a projectName already exists
    public boolean isProjectNameExists(String projectName) {
        Optional<Project> existingProject = projectRepository.findByProjectName(projectName);
        return existingProject.isPresent();
    }


    public Project save(Project project) {
        projectRepository.save(project);
        return project;
    }

    public Optional<Project> findById(long projectId) {
        return projectRepository.findById(projectId);
    }

    public List<Project> getAllProjectsWithCounts() {
        List<Project> projects = projectRepository.findAll();

        for (Project project : projects) {
            // Initialize collections to trigger lazy loading
            project.getUsers().size();
//            project.getIssues().size();
        }

        return projects;
    }

    public int findNumberOfIssuesByProjectId(long projectId) {
        return projectRepository.findNumberOfIssuesByProjectId(projectId);
    }

    // Method to get projects by user IDs
    public Set<Project> getProjectsByUserId(long userId) {
        return projectRepository.findProjectsByUserId(userId);
    }


//    @Transactional
//    public void deleteProjectById(long projectId) {
//        // Perform the deletion operation
//        projectRepository.deleteById(projectId);
//
//        // Redirect to the project list page after deletion
//        new RedirectView("/project-list", true);
//    }

    @Transactional
    public void deleteProjectById(long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        projectOptional.ifPresent(project -> {
            // Update the status of the project
            project.setStatus("DELETED");
            // Save the updated project
            projectRepository.save(project);
        });
    }

    public Project getProjectById(long projectId) {
        // Fetch the project details from the repository based on the project ID
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        // Return the project if found, otherwise return null
        return optionalProject.orElse(null);
    }

    public Set<Issue> getIssuesByProjectId(long projectId) {
        // Retrieve issues from the database based on projectId
        return issueRepository.findByProjectId(projectId);
    }

    public Project findProjectById(long id){
        return projectRepository.findProjectById(id);
    }

    public Set<Project> getProjectsByUsersId(Long id) {
        return projectRepository.findProjectsByUserId(id);
    }

    public boolean isProjectExists(long id) {
        return projectRepository.existsById(id);
    }


    public void addUsersToProject(int projectId, List<Integer> userIds) throws ChangeSetPersister.NotFoundException {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            Set<User> users = project.getUsers();
            UserService userService = new UserService(); // Assuming you have a UserService
            for (Integer userId : userIds) {
                Optional<User> userOptional = Optional.ofNullable(userService.getUserById(userId));
                userOptional.ifPresent(users::add); // Add user to the project's set of users
            }
            projectRepository.save(project);
        } else {
            // Handle case where project with given ID is not found
            throw new ChangeSetPersister.NotFoundException();
        }
    }
}
