package com.chat.kasa.service;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {

    ChatRoom createChatRoom(Profile user1, Profile user2);

    Optional<ChatRoom> getChatRoom(Long id);

    Optional<ChatRoom> getChatRoom(Profile user1, Profile user2);

    List<ChatRoom> getAllChatRoomsForUser(Profile user);
}
