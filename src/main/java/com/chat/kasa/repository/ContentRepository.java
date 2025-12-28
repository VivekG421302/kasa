package com.chat.kasa.repository;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Content;
import com.chat.kasa.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    // Find all messages by chatroom
    List<Content> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    // Find all messages by sender
    List<Content> findBySender(Profile sender);
}
