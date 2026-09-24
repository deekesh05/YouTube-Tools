package com.youtubetools.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String toolType;

    private String videoId;

    @Column(length = 500)
    private String title;

    @Column(length = 1000)
    private String thumbnailUrl;

    @Column(length = 2000)
    private String input;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}