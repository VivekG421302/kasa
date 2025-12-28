package com.chat.kasa.repository;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // Find a chat room between two users
    Optional<ChatRoom> findByUser1AndUser2(Profile user1, Profile user2);

    // Get all chat rooms for a user
    List<ChatRoom> findByUser1OrUser2(Profile user1, Profile user2);
}
