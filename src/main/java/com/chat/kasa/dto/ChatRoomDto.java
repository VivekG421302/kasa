package com.chat.kasa.dto;

import com.chat.kasa.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private UserDto userA;
    private UserDto userB;
    private LocalDateTime createdAt;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.getId(),
                UserDto.fromEntity(chatRoom.getUserA()),
                UserDto.fromEntity(chatRoom.getUserB()),
                chatRoom.getCreatedAt()
        );
    }
}
