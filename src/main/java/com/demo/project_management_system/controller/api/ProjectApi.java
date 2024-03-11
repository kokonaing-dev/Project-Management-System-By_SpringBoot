package com.demo.project_management_system.controller.api;

import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectApi {

    private final ProjectService projectService;

    @GetMapping("/getProjectName")
    public String getProjectName(@RequestParam Long projectId) {
        // Assuming your ProjectService has a method to fetch the project name by ID
        Project project = projectService.getProjectById(projectId);
        String projectName = project.getProjectName();
        return "{\"projectName\": \"" + projectName + "\"}";
    }

}
