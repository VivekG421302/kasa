package com.chat.kasa.repository;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.userA = :user1 AND cr.userB = :user2) OR (cr.userA = :user2 AND cr.userB = :user1)")
    Optional<ChatRoom> findChatRoomByUsers(@Param("user1") User user1, @Param("user2") User user2);
}
