package com.demo.project_management_system.controller.api;

import com.demo.project_management_system.entity.ChatMessage;
import com.demo.project_management_system.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageApi {
    private final ChatMessageService messageService ;

    @GetMapping("/chats")
    public ResponseEntity<List<ChatMessage>> getChatMessagesByProjectId(@RequestParam("id") Long projectId) {
        List<ChatMessage> chatMessages = messageService.findByProjectId(projectId);

        //user contians as object ...
        System.out.println("Chat Messages..." + chatMessages);
        return ResponseEntity.ok(chatMessages);
    }


}
