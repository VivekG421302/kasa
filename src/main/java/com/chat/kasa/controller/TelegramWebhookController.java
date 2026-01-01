package com.chat.kasa.controller;

import com.chat.kasa.telegram.KasaBot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
public class TelegramWebhookController {

    private final KasaBot kasaBot;

    @PostMapping("${telegram.bot.webhook-path}")
    public ResponseEntity<BotApiMethod<?>> onUpdateReceived(@RequestBody Update update) {
        BotApiMethod<?> response = kasaBot.onWebhookUpdateReceived(update);
        return ResponseEntity.ok(response);
    }
}
