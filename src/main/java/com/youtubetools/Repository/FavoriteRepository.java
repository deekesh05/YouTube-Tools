package com.youtubetools.Repository;

import com.youtubetools.Entity.Favorite;
import com.youtubetools.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    // Current user's favorites
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);

    // Check whether video is already favorite
    boolean existsByUserAndVideoId(
            User user,
            String videoId
    );

    // Find particular favorite
    Optional<Favorite> findByUserAndVideoId(
            User user,
            String videoId
    );

    // Delete favorite belonging to current user
    void deleteByIdAndUser(
            Long id,
            User user
    );

    // Count user's favorites
    long countByUser(User user);
}