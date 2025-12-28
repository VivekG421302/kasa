package com.chat.kasa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String displayName;

    @Column
    private String email;

//    // One profile can have many messages
//    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
//    private List<Content> contents;
//
//    // One profile can be part of multiple chatrooms (as user1 or user2)
//    @OneToMany(mappedBy = "user1")
//    private List<ChatRoom> chatRoomsAsUser1;
//
//    @OneToMany(mappedBy = "user2")
//    private List<ChatRoom> chatRoomsAsUser2;
}
