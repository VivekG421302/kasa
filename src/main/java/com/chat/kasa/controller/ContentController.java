package com.chat.kasa.controller;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Content;
import com.chat.kasa.model.Profile;
import com.chat.kasa.service.ChatRoomService;
import com.chat.kasa.service.ContentService;
import com.chat.kasa.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ChatRoomService chatRoomService;
    private final ProfileService profileService;

    @Value("${telegram.bot-token}")
    private String BOT_TOKEN;

    // Send text message
    @PostMapping("/text")
    public ResponseEntity<Content> sendText(
            @RequestParam Long senderId,
            @RequestParam Long chatRoomId,
            @RequestParam String messageText
    ) {
        Profile sender = profileService.getProfileById(senderId).orElse(null);
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId).orElse(null);
        if (sender == null || chatRoom == null) return ResponseEntity.badRequest().build();

        Content content = Content.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .messageText(messageText)
                .mediaType(Content.MediaType.TEXT)
                .build();

        return ResponseEntity.ok(contentService.saveMessage(content));
    }

    // Send media (image, video, document, voice, audio, etc)
    @PostMapping("/media")
    public ResponseEntity<Content> sendMedia(
            @RequestParam Long senderId,
            @RequestParam Long chatRoomId,
            @RequestParam MultipartFile file
    ) throws IOException {
        Profile sender = profileService.getProfileById(senderId).orElse(null);
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId).orElse(null);
        if (sender == null || chatRoom == null) return ResponseEntity.badRequest().build();

        Content content = contentService.saveMediaMessage(sender, chatRoom, file);
        return ResponseEntity.ok(content);
    }

    // Get all messages for a chatroom
    @GetMapping("/chatroom/{chatRoomId}")
    public ResponseEntity<List<Content>> getMessages(@PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId).orElse(null);
        if (chatRoom == null) return ResponseEntity.notFound().build();

        List<Content> messages = contentService.getMessagesByChatRoom(chatRoom);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<byte[]> getMedia(@PathVariable String fileId) throws IOException {
        // Get file path from Telegram
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> response = restTemplate.getForObject(
                "https://api.telegram.org/bot" + BOT_TOKEN + "/getFile?file_id=" + fileId,
                Map.class
        );

        Map<String, String> result = (Map<String, String>) response.get("result");
        String filePath = result.get("file_path");

        // Download file bytes
        byte[] bytes = restTemplate.getForObject(
                "https://api.telegram.org/file/bot" + BOT_TOKEN + "/" + filePath,
                byte[].class
        );

        // Determine content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // optional: detect MIME from file extension
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}
