package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.ChatMessage;
import com.demo.project_management_system.service.ChatMessageService;
import com.demo.project_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class MessageController {

    private final ChatMessageService messageService ;

    @MessageMapping("/{projectId}/connectUser")
    @SendTo("/topic/{projectId}/messages")
    public List<ChatMessage> addUser(@Payload ChatMessage message,
                                     SimpMessageHeaderAccessor headerAccessor,
                                     @DestinationVariable String projectId) {
        // Logging for debugging
        System.out.println("Received message for /app/{}/addUser");
        // Add username to the WebSocket session
        headerAccessor.getSessionAttributes().put("username", message.getUser().getUsername());
        return messageService.findByProjectId(message.getProject().getId());
    }

    @MessageMapping("/{projectId}/sendMessage")
    @SendTo("/topic/{projectId}/messages")
    public List<ChatMessage> handleChatMessages(@Payload ChatMessage message, @DestinationVariable String projectId) {
        // Logging for debugging
        System.out.println("Received message for /app/{}/sendMessages");
        System.out.println(message);
        // Save the new message to the database
        messageService.save(message);
        // Retrieve the list of messages from the database
        return messageService.findByProjectId(message.getProject().getId());
    }



}