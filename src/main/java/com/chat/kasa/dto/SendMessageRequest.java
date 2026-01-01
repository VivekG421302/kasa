package com.chat.kasa.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {
    @NotNull
    private Long senderId;
    @NotNull
    private Long receiverId;
    @NotNull
    private String text;
}
