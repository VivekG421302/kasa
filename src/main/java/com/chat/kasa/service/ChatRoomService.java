package com.chat.kasa.service;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.User;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    ChatRoom findOrCreateChatRoom(User user1, User user2);
    Optional<ChatRoom> findById(Long id);
    List<ChatRoom> findAll();
}
