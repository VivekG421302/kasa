package com.chat.kasa.service.impl;

import com.chat.kasa.model.User;
import com.chat.kasa.repository.UserRepository;
import com.chat.kasa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User findOrCreateUser(Message message) {
        Long telegramUserId = message.getFrom().getId();
        return userRepository.findByTelegramUserId(telegramUserId)
                .map(user -> {
                    // Update username if it has changed
                    String newUsername = message.getFrom().getUserName();
                    if (newUsername != null && !newUsername.equals(user.getUsername())) {
                        user.setUsername(newUsername);
                        return userRepository.save(user);
                    }
                    return user;
                })
                .orElseGet(() -> {
                    String username = message.getFrom().getUserName() != null ? message.getFrom().getUserName() : "user_" + telegramUserId;
                    User newUser = new User(telegramUserId, username, message.getChatId());
                    return userRepository.save(newUser);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByTelegramUserId(Long telegramUserId) {
        return userRepository.findByTelegramUserId(telegramUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
