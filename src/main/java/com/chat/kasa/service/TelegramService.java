package com.chat.kasa.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;

public interface TelegramService {
    Message execute(BotApiMethod<? extends Serializable> method);
}
