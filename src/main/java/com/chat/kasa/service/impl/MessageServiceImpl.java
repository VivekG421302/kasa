package com.chat.kasa.service.impl;

import com.chat.kasa.model.*;
import com.chat.kasa.model.Message;
import com.chat.kasa.repository.MessageRepository;
import com.chat.kasa.service.ChatRoomService;
import com.chat.kasa.service.MessageService;
import com.chat.kasa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.Audio;


import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @Value("${kasa.bot.admin-telegram-id}")
    private Long adminTelegramId;

    @Override
    @Transactional
    public com.chat.kasa.model.Message saveMessage(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        org.telegram.telegrambots.meta.api.objects.Message telegramMessage = update.getMessage();

        // 1. Find or create the sender
        User sender = userService.findOrCreateUser(telegramMessage);

        // 2. Determine the receiver. For now, it's the admin.
        User receiver = userService.findByTelegramUserId(adminTelegramId)
                .orElseThrow(() -> new IllegalStateException("Admin user with ID " + adminTelegramId + " not found in the database. Please register the admin user first."));

        // If the sender is the admin, we cannot determine the receiver from this message alone.
        // This logic needs to be handled in the UpdateRouter for commands like /reply <userId> <message>
        // For now, we'll assume admin doesn't initiate conversations this way.
        if (sender.getTelegramUserId().equals(adminTelegramId)) {
            log.warn("Admin user sent a message without a specific recipient context. Ignoring.");
            // In a real scenario, you'd have a mechanism for the admin to specify the recipient.
            // For example, by replying to a user's message, or using a command.
            return null;
        }

        // 3. Find or create the chat room
        ChatRoom chatRoom = chatRoomService.findOrCreateChatRoom(sender, receiver);

        // 4. Create and populate the message entity
        com.chat.kasa.model.Message message = new com.chat.kasa.model.Message();
        message.setTelegramMessageId(telegramMessage.getMessageId());
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setReceiver(receiver);

        // 5. Populate message based on type
        if (telegramMessage.hasText()) {
            message.setMessageType(MessageType.TEXT);
            message.setText(telegramMessage.getText());
        } else if (telegramMessage.hasPhoto()) {
            message.setMessageType(MessageType.PHOTO);
            // Get the largest photo
            PhotoSize photo = telegramMessage.getPhoto().stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
            if (photo != null) {
                message.setFileId(photo.getFileId());
                message.setFileSize(photo.getFileSize().longValue());
            }
        } else if (telegramMessage.hasVideo()) {
            message.setMessageType(MessageType.VIDEO);
            Video video = telegramMessage.getVideo();
            message.setFileId(video.getFileId());
            message.setMimeType(video.getMimeType());
            message.setFileSize(video.getFileSize());
        } else if (telegramMessage.hasVoice()) {
            message.setMessageType(MessageType.VOICE);
            Voice voice = telegramMessage.getVoice();
            message.setFileId(voice.getFileId());
            message.setMimeType(voice.getMimeType());
            message.setFileSize(voice.getFileSize());
        } else if (telegramMessage.hasAudio()) {
            message.setMessageType(MessageType.AUDIO);
            Audio audio = telegramMessage.getAudio();
            message.setFileId(audio.getFileId());
            message.setMimeType(audio.getMimeType());
            message.setFileSize(audio.getFileSize());
        } else if (telegramMessage.hasDocument()) {
            message.setMessageType(MessageType.DOCUMENT);
            Document document = telegramMessage.getDocument();
            message.setFileId(document.getFileId());
            message.setMimeType(document.getMimeType());
            message.setFileSize(document.getFileSize());
        } else {
            log.warn("Received a message with an unsupported type. Message ID: {}", telegramMessage.getMessageId());
            return null; // Or handle as an unsupported type
        }

        log.debug("Saving message from user {} in chat room {}", sender.getTelegramUserId(), chatRoom.getId());
        return messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.chat.kasa.model.Message> getMessagesForChatRoom(ChatRoom chatRoom) {
        return messageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);
    }
}
