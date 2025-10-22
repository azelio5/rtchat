package com.sbbc.rch.realchatapp.listener;

import com.sbbc.rch.realchatapp.model.ChatMessage;
import com.sbbc.rch.realchatapp.model.MessageType;
import com.sbbc.rch.realchatapp.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketListener {

    private final UserService userService;
    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketListener(UserService userService, SimpMessageSendingOperations messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Connected to RealChatApp");

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        //   String username = headerAccessor.getSessionAttributes().get("username").toString();
        String username = headerAccessor.getUser().getName();
        userService.setUserOnlineStatus(username, false);

        log.info("User {} disconnected", username);

        ChatMessage chatMessage = ChatMessage.builder()
                .type(MessageType.LEAVE)
                .sender(username)
                .build();
        messagingTemplate.convertAndSend("/topic/public", chatMessage);

    }
}
