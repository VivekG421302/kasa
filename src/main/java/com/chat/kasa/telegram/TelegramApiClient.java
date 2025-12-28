//package com.chat.kasa.telegram;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class TelegramApiClient {
//
//    @Value("${telegram.bot-token}")
//    private String botToken;
//
//    @Value("${telegram.channel-id}")
//    private String channelId;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public void sendTextToChannel(String text) {
//        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("chat_id", channelId);
//        body.put("text", text);
//
//        restTemplate.postForObject(url, body, String.class);
//    }
//}
