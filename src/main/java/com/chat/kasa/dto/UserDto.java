package com.chat.kasa.dto;

import com.chat.kasa.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private Long telegramUserId;
    private String username;
    private Long chatId;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;

    public static UserDto fromEntity(User user) {
        return new UserDto(
                user.getId(),
                user.getTelegramUserId(),
                user.getUsername(),
                user.getChatId(),
                user.getFirstSeen(),
                user.getLastSeen()
        );
    }
}
