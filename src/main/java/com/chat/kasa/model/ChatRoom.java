package com.chat.kasa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "chat_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User1
    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private Profile user1;

    // User2
    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private Profile user2;

//    // Messages sent in this chatroom
//    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
//    private List<Content> messages;
}
