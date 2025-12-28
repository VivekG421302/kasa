package com.chat.kasa.service;

import com.chat.kasa.model.ChatRoom;
import com.chat.kasa.model.Content;
import com.chat.kasa.model.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ContentService {

    Content saveMessage(Content content);

    List<Content> getMessagesByChatRoom(ChatRoom chatRoom);

    List<Content> getMessagesBySender(Profile sender);

    Content saveMediaMessage(Profile sender, ChatRoom chatRoom, MultipartFile file) throws IOException;    }
