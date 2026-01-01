package com.chat.kasa.controller.api;

import com.chat.kasa.dto.ChatRoomDto;
import com.chat.kasa.dto.MessageDto;
import com.chat.kasa.dto.SendMessageRequest;
import com.chat.kasa.dto.UserDto;
import com.chat.kasa.exception.ResourceNotFoundException;
import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Message;
import com.chat.kasa.model.User;
import com.chat.kasa.service.ChatRoomService;
import com.chat.kasa.service.MessageService;
import com.chat.kasa.service.TelegramService;
import com.chat.kasa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final TelegramService telegramService;

    @GetMapping("/users")
    public List<UserDto> listUsers() {
        return userService.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/chats")
    public List<ChatRoomDto> listChatRooms() {
        return chatRoomService.findAll().stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/chats/{chatId}/messages")
    public List<MessageDto> listMessagesForChatRoom(@PathVariable Long chatId) {
        ChatRoom chatRoom = chatRoomService.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom not found with id: " + chatId));
        return messageService.getMessagesForChatRoom(chatRoom).stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/messages")
    public ResponseEntity<Void> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        User sender = userService.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with id: " + request.getSenderId()));
        User receiver = userService.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + request.getReceiverId()));

        // Create the Telegram message
        SendMessage telegramMessage = new SendMessage();
        telegramMessage.setChatId(receiver.getChatId().toString());
        telegramMessage.setText(request.getText());
        
        // Send the message via Telegram
        telegramService.execute(telegramMessage);

        // Optionally, save the message sent via API to our own DB
        // This requires more logic to handle ChatRoom, etc.
        // For now, we just send it. The user request was "Sending messages via Telegram".

        return ResponseEntity.ok().build();
    }
}
