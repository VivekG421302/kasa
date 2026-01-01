package com.chat.kasa.service.impl;

import com.chat.kasa.model.Message;
import com.chat.kasa.service.TelegramService;
import com.chat.kasa.telegram.KasaBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Service
@Slf4j
public class TelegramServiceImpl implements TelegramService {

    private final KasaBot kasaBot;

    // Use @Lazy to break circular dependency
    public TelegramServiceImpl(@Lazy KasaBot kasaBot) {
        this.kasaBot = kasaBot;
    }

    @Override
    public Message execute(BotApiMethod<? extends Serializable> method) {
        try {
            Serializable result = kasaBot.execute(method);
            if (result instanceof Message) {
                return (Message) result;
            }
        } catch (TelegramApiException e) {
            log.error("Error executing Telegram method: {}", e.getMessage());
            // Depending on the error, you might want to throw a custom exception
        }
        return null;
    }
}
