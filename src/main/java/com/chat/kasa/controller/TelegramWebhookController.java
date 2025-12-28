package com.chat.kasa.controller;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Content;
import com.chat.kasa.model.Profile;
import com.chat.kasa.service.ChatRoomService;
import com.chat.kasa.service.ContentService;
import com.chat.kasa.service.ProfileService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/telegram/webhook")
@RequiredArgsConstructor
public class TelegramWebhookController {

    @Value("${telegram.bot-token}")
    private String BOT_TOKEN;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ContentService contentService;
    private final ProfileService profileService;
    private final ChatRoomService chatRoomService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> onUpdate(@RequestBody String payload) {
        try {
            JsonNode update = objectMapper.readTree(payload);

            if (update.has("message")) {
                JsonNode message = update.get("message");
                Long telegramUserId = message.get("from").get("id").asLong();

                Optional<Profile> senderOpt = profileService.getProfileById(telegramUserId); // implement mapping
                if (senderOpt.isEmpty()) return ResponseEntity.ok("Sender not mapped");

                Profile sender = senderOpt.get();

                // Default chatroom (or you can map to a specific chatroom)
                Optional<ChatRoom> chatRoomOpt = chatRoomService.getAllChatRoomsForUser(sender).stream().findFirst();
                if (chatRoomOpt.isEmpty()) return ResponseEntity.ok("ChatRoom not found");

                ChatRoom chatRoom = chatRoomOpt.get();

                Content.MediaType contentType = Content.MediaType.TEXT;
                String text = null;
                String mediaPath = null;

                // Text message
                if (message.has("text")) {
                    text = message.get("text").asText();
                    contentType = Content.MediaType.TEXT;
                }

                // Photo
                else if (message.has("photo")) {
                    JsonNode photoArray = message.get("photo");
                    JsonNode largestPhoto = photoArray.get(photoArray.size() - 1);
                    String fileId = largestPhoto.get("file_id").asText();
                    mediaPath = downloadFileFromTelegram(fileId);
                    contentType = Content.MediaType.IMAGE;
                }

                // Document
                else if (message.has("document")) {
                    String fileId = message.get("document").get("file_id").asText();
                    mediaPath = downloadFileFromTelegram(fileId);
                    contentType = Content.MediaType.DOCUMENT;
                }

                // Video
                else if (message.has("video")) {
                    String fileId = message.get("video").get("file_id").asText();
                    mediaPath = downloadFileFromTelegram(fileId);
                    contentType = Content.MediaType.VIDEO;
                }

                // Voice / Audio
                else if (message.has("voice")) {
                    String fileId = message.get("voice").get("file_id").asText();
                    mediaPath = downloadFileFromTelegram(fileId);
                    contentType = Content.MediaType.VOICE;
                } else if (message.has("audio")) {
                    String fileId = message.get("audio").get("file_id").asText();
                    mediaPath = downloadFileFromTelegram(fileId);
                    contentType = Content.MediaType.AUDIO;
                }

                // Save content
                Content content = Content.builder()
                        .sender(sender)
                        .chatRoom(chatRoom)
                        .messageText(text)
                        .mediaType(contentType)
                        .mediaPath(mediaPath)
                        .build();

                contentService.saveMessage(content);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }

        return ResponseEntity.ok("OK");
    }

    private String downloadFileFromTelegram(String fileId) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String getFileUrl = "https://api.telegram.org/bot" + BOT_TOKEN + "/getFile?file_id=" + fileId;
        Map<String, Object> response = restTemplate.getForObject(getFileUrl, Map.class);
        Map<String, String> result = (Map<String, String>) response.get("result");
        String filePath = result.get("file_path");

        String downloadUrl = "https://api.telegram.org/file/bot" + BOT_TOKEN + "/" + filePath;
        byte[] fileBytes = restTemplate.getForObject(downloadUrl, byte[].class);

        // Save locally (C:/uploads/)
        String localPath = "C:/uploads/" + System.currentTimeMillis() + "_" + filePath.substring(filePath.lastIndexOf("/")+1);
        java.nio.file.Files.write(java.nio.file.Paths.get(localPath), fileBytes);
        return localPath;
    }
}
