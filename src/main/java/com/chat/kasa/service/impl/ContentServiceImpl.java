package com.chat.kasa.service.impl;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Content;
import com.chat.kasa.model.Profile;
import com.chat.kasa.repository.ContentRepository;
import com.chat.kasa.service.ContentService;
import com.chat.kasa.service.TelegramApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final TelegramApiClient telegramApiClient;

    private final String uploadDir = "C:/uploads"; // Change to your desired path

    @Override
    public Content saveMessage(Content content) {
        // Save to database
        Content saved = contentRepository.save(content);

        // Send to Telegram
        String telegramMessage = "[" + content.getSender().getUsername() + "] ";
        if (content.getMessageText() != null) telegramMessage += content.getMessageText();
        if (content.getMediaType() != null) telegramMessage += " [" + content.getMediaType() + "]";
        telegramApiClient.sendTextToChannel(telegramMessage);

        return saved;
    }

    @Override
    public Content saveMediaMessage(Profile sender, ChatRoom chatRoom, MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        Content.MediaType mediaType = detectMediaType(extension);

        // Upload file to Telegram
        String telegramFileId = telegramApiClient.uploadFile(file, mediaType); // Implement this method

        Content content = Content.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .mediaType(mediaType)
                .mediaPath(telegramFileId) // store Telegram file_id
                .build();

        return saveMessage(content);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return dotIndex == -1 ? "" : filename.substring(dotIndex + 1);
    }

    private Content.MediaType detectMediaType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif" -> Content.MediaType.IMAGE;
            case "mp4", "mkv", "avi" -> Content.MediaType.VIDEO;
            case "pdf", "docx", "xlsx", "csv", "zip", "rar", "war" -> Content.MediaType.DOCUMENT;
            case "mp3", "wav" -> Content.MediaType.AUDIO;
            case "ogg", "m4a" -> Content.MediaType.VOICE;
            default -> Content.MediaType.OTHER;
        };
    }

    @Override
    public List<Content> getMessagesByChatRoom(ChatRoom chatRoom) {
        return contentRepository.findByChatRoomOrderByTimestampAsc(chatRoom);
    }

    @Override
    public List<Content> getMessagesBySender(Profile sender) {
        return contentRepository.findBySender(sender);
    }
}
