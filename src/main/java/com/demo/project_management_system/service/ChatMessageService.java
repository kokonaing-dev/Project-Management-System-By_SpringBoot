package com.demo.project_management_system.service;

import com.demo.project_management_system.entity.ChatMessage;
import com.demo.project_management_system.entity.Project;
import com.demo.project_management_system.entity.User;
import com.demo.project_management_system.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final ProjectService projectService;


    public void save(ChatMessage chatMessage) {
        // Get the sender user
        User sender = userService.findUserById(chatMessage.getUser().getId());
        //Get the project
        Project project = projectService.findProjectById( chatMessage.getProject().getId());

        chatMessage.setUser(sender);
        chatMessage.setProject(project);

        // Set the timestamp (assuming you want to set the current time)
        chatMessage.setTimestamp(new Date());

        // Save the chat message
        chatMessageRepository.save(chatMessage);

    }

    public List<ChatMessage> findByProjectId(Long projectId) {
        // Check if the project exists (you can add additional checks if needed)
        if (projectService.isProjectExists(projectId)) {
            // Find chat messages by project ID
            return chatMessageRepository.findByProjectId(projectId);
        } else {
            // Throw an exception or handle the case where the specified project does not exist
            throw new RuntimeException("Specified project does not exist.");
        }


    }

}
