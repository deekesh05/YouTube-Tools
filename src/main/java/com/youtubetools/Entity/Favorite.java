package com.youtubetools.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_video",
                        columnNames = {"user_id", "video_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "video_id",
            nullable = false
    )
    private String videoId;

    @Column(length = 500)
    private String title;

    @Column(length = 1000)
    private String thumbnailUrl;

    @Column(length = 500)
    private String channelTitle;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}