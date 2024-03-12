package com.demo.project_management_system.controller;

import com.demo.project_management_system.entity.ChatMessage;
import com.demo.project_management_system.service.ChatMessageService;
import com.demo.project_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MessageController {

    private final ChatMessageService messageService ;

    @MessageMapping("/{projectId}/connectUser")
    @SendTo("/topic/{projectId}/messages")
    public List<ChatMessage> addUser(@Payload ChatMessage message,
                                     SimpMessageHeaderAccessor headerAccessor,
                                     @DestinationVariable String projectId) {

        // Add username to the WebSocket session
        headerAccessor.getSessionAttributes().put("username", message.getUser().getUsername());
        return messageService.findByProjectId(message.getProject().getId());
    }

    @MessageMapping("/{projectId}/sendMessage")
    @SendTo("/topic/{projectId}/messages")
    public List<ChatMessage> handleChatMessages(@Payload ChatMessage message, @DestinationVariable String projectId) {

        log.info("Message : " + message);
        // Save the new message to the database
        messageService.save(message);

        log.info("Message List " + messageService.findByProjectId(message.getProject().getId()));
        // Retrieve the list of messages from the database
        return messageService.findByProjectId(message.getProject().getId());
    }



}