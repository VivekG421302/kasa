package com.chat.kasa.service.impl;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Profile;
import com.chat.kasa.repository.ChatRoomRepository;
import com.chat.kasa.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ChatRoom createChatRoom(Profile user1, Profile user2) {
        // Check if chat room already exists
        Optional<ChatRoom> existing = chatRoomRepository.findByUser1AndUser2(user1, user2);
        if (existing.isPresent()) return existing.get();

        ChatRoom chatRoom = ChatRoom.builder()
                .user1(user1)
                .user2(user2)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public Optional<ChatRoom> getChatRoom(Long id) {
        return chatRoomRepository.findById(id);
    }

    @Override
    public Optional<ChatRoom> getChatRoom(Profile user1, Profile user2) {
        return chatRoomRepository.findByUser1AndUser2(user1, user2);
    }

    @Override
    public List<ChatRoom> getAllChatRoomsForUser(Profile user) {
        return chatRoomRepository.findByUser1OrUser2(user, user);
    }
}
