package com.chat.kasa.service;

import com.chat.kasa.model.Content;
import com.chat.kasa.service.impl.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class TelegramApiClient {

    @Value("${telegram.bot-token}")
    private String BOT_TOKEN;

    @Value("${telegram.channel-id}")
    private String TELEGRAM_CHANNEL_ID;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendTextToChannel(String text) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", TELEGRAM_CHANNEL_ID);
        body.put("text", text);

        restTemplate.postForObject(url, body, String.class);
    }

    public String uploadFile(MultipartFile file, Content.MediaType mediaType) throws IOException {
        String url;
        switch (mediaType) {
            case IMAGE -> url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendPhoto";
            case VIDEO -> url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendVideo";
            case DOCUMENT -> url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendDocument";
            case VOICE, AUDIO -> url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendVoice";
            default -> url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendDocument";
        }

        // Use RestTemplate with MultiPart
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("chat_id", TELEGRAM_CHANNEL_ID); // your bot/channel
        body.add("document", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(url, new HttpEntity(body) {
        }, Map.class);

        Map<String, Object> result = (Map<String, Object>) response.get("result");
        Map<String, String> fileObj = (Map<String, String>) result.get("document"); // or "photo" for images
        return fileObj.get("file_id");
    }

}
