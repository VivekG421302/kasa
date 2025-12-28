package com.chat.kasa.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    public enum MediaType {
        TEXT, IMAGE, VIDEO, DOCUMENT, VOICE, AUDIO, OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The profile who sent the message
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Profile sender;

    // The chatroom where this message belongs
    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(length = 5000)
    private String messageText;

    @Column
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column
    private String mediaPath; // Local path or Telegram file_id

    @Column
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) timestamp = LocalDateTime.now();
    }
}
