package com.demo.project_management_system.config;

import com.demo.project_management_system.entity.ChatMessage;
import com.demo.project_management_system.entity.MessageType;
import com.demo.project_management_system.entity.User;
import com.demo.project_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("user disconnected: {}", username);

            // Retrieve the User entity associated with the disconnected user
            User disconnectedUser = userRepository.findByUsername(username);

            if (disconnectedUser != null) {
                var chatMessage = ChatMessage.builder()
                        .messageType(MessageType.LEAVE)
                        .user(disconnectedUser)  // Set the User entity as the sender
                        .build();
                messagingTemplate.convertAndSend("/topic/public", chatMessage);
            }
        }

    }
}
