package com.chat.kasa.controller;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Profile;
import com.chat.kasa.service.ChatRoomService;
import com.chat.kasa.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ProfileService profileService;

    // Create a chatroom between two users
    @PostMapping
    public ResponseEntity<ChatRoom> createChatRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        Profile user1 = profileService.getProfileById(user1Id).orElse(null);
        Profile user2 = profileService.getProfileById(user2Id).orElse(null);

        if (user1 == null || user2 == null) {
            return ResponseEntity.badRequest().build();
        }

        ChatRoom chatRoom = chatRoomService.createChatRoom(user1, user2);
        return ResponseEntity.ok(chatRoom);
    }

    // Get all chatrooms for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatRoom>> getAllChatRooms(@PathVariable Long userId) {
        Profile user = profileService.getProfileById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        List<ChatRoom> chatRooms = chatRoomService.getAllChatRoomsForUser(user);
        return ResponseEntity.ok(chatRooms);
    }
}
