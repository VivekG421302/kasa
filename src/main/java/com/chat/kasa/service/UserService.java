package com.chat.kasa.service;

import com.chat.kasa.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findOrCreateUser(Message message);
    Optional<User> findByTelegramUserId(Long telegramUserId);
    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
}
