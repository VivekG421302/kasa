package com.chat.kasa.service;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface MessageService {
    Message saveMessage(Update update);
    List<Message> getMessagesForChatRoom(ChatRoom chatRoom);
}
