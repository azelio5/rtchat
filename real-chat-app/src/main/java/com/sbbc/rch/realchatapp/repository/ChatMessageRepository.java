package com.sbbc.rch.realchatapp.repository;

import com.sbbc.rch.realchatapp.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    @Query("SELECT cm from ChatMessage cm WHERE cm.type = 'PRIVATE' " +
            "AND cm.sender =:user1 " +
            "AND cm.receiver =:user2 " +
            "OR  cm.receiver =:user1 " +
            "AND cm.sender =:user2 " +
            "ORDER BY cm.timestamp ASC")
    List<ChatMessage> findPrivateMessagesBetweenTwoUsers(@Param("user1") String user1,
                                                         @Param("user2") String user2);
}
