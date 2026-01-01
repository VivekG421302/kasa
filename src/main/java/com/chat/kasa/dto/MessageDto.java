package com.chat.kasa.dto;

import com.chat.kasa.model.Message;
import com.chat.kasa.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private Integer telegramMessageId;
    private Long chatRoomId;
    private Long senderId;
    private Long receiverId;
    private MessageType messageType;
    private String text;
    private String fileId;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime createdAt;

    public static MessageDto fromEntity(Message message) {
        return new MessageDto(
                message.getId(),
                message.getTelegramMessageId(),
                message.getChatRoom().getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getMessageType(),
                message.getText(),
                message.getFileId(),
                message.getMimeType(),
                message.getFileSize(),
                message.getCreatedAt()
        );
    }
}
