package com.sbbc.rch.realchatapp.controllers;

import com.sbbc.rch.realchatapp.model.ChatMessage;
import com.sbbc.rch.realchatapp.repository.ChatMessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final ChatMessageRepository chatMessageRepository;

    public MessageController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ResponseEntity<List<ChatMessage>> getPrivateMessages(@RequestParam String user1,
                                                                @RequestParam String user2) {

        List<ChatMessage> messages = chatMessageRepository.findPrivateMessagesBetweenTwoUsers(user1, user2);
        return new ResponseEntity<>(messages, HttpStatus.OK);

    }
}
