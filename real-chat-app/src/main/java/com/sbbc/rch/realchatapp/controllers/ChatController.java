package com.sbbc.rch.realchatapp.controllers;

import com.sbbc.rch.realchatapp.model.ChatMessage;
import com.sbbc.rch.realchatapp.model.MessageType;
import com.sbbc.rch.realchatapp.repository.ChatMessageRepository;
import com.sbbc.rch.realchatapp.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
public class ChatController {

    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public ChatController(UserService userService, ChatMessageRepository chatMessageRepository, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        if (userService.userExists(chatMessage.getSender())) {
            //store username in session
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            userService.setUserOnlineStatus(chatMessage.getSender(), true);

            log.info("User added successfully {} with session ID: {}",
                    chatMessage.getSender(), headerAccessor.getSessionId());

            chatMessage = ChatMessage.builder()
                    .timestamp(LocalDateTime.now())
                    .content(chatMessage.getContent() == null ? "" : chatMessage.getContent())
                    .build();
            return chatMessageRepository.save(chatMessage);
        }
        log.warn("User {} does not exist", chatMessage.getSender());
        return null;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if (userService.userExists(chatMessage.getSender())) {
            chatMessage = ChatMessage.builder()
                    .timestamp(chatMessage.getTimestamp() == null ? LocalDateTime.now() : chatMessage.getTimestamp())
                    .content(chatMessage.getContent() == null ? "" : chatMessage.getContent())
                    .build();
            log.info("Message sent successfully {}", chatMessage);
            return chatMessageRepository.save(chatMessage);
        }
        log.warn("Message sending error with ID:{}", chatMessage.getId());
        return null;

    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage == null || chatMessage.getSender() == null || chatMessage.getReceiver() == null) {
            log.warn("Invalid chat message payload: {}", chatMessage);
            return;
        }

        if (!userService.userExists(chatMessage.getSender()) ||
                !userService.userExists(chatMessage.getReceiver())) {
            log.warn("Sender {} or Receiver {} does not exist", chatMessage.getSender(), chatMessage.getReceiver());
            return;
        }

        chatMessage.setTimestamp(chatMessage.getTimestamp() == null ? LocalDateTime.now() : chatMessage.getTimestamp());
        chatMessage.setContent(chatMessage.getContent() == null ? "" : chatMessage.getContent());
        chatMessage.setType(MessageType.PRIVATE);

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        log.info("Message saved successfully with ID: {}", savedMessage.getId());

        try {
            messagingTemplate.convertAndSend("/user/" + chatMessage.getReceiver() + "/queue/private", savedMessage);
            messagingTemplate.convertAndSend("/user/" + chatMessage.getSender() + "/queue/private", savedMessage);
        } catch (Exception e) {
            log.error("Error while sending message", e);
        }
    }

}
