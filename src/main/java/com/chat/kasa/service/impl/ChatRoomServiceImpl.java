package com.chat.kasa.service.impl;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.User;
import com.chat.kasa.repository.ChatRoomRepository;
import com.chat.kasa.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public ChatRoom findOrCreateChatRoom(User user1, User user2) {
        return chatRoomRepository.findChatRoomByUsers(user1, user2)
                .orElseGet(() -> {
                    ChatRoom newChatRoom = new ChatRoom(user1, user2);
                    return chatRoomRepository.save(newChatRoom);
                });
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ChatRoom> findById(Long id) {
        return chatRoomRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }
}
