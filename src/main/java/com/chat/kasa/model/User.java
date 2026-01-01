package com.chat.kasa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_telegram_user_id", columnList = "telegramUserId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramUserId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long chatId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime firstSeen;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastSeen;

    public User(Long telegramUserId, String username, Long chatId) {
        this.telegramUserId = telegramUserId;
        this.username = username;
        this.chatId = chatId;
    }
}
