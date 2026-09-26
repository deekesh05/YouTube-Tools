package com.youtubetools.Service;

import com.youtubetools.Entity.Favorite;
import com.youtubetools.Entity.User;
import com.youtubetools.Repository.FavoriteRepository;
import com.youtubetools.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;


    // =========================================================
    // ADD FAVORITE
    // =========================================================

    public boolean addFavorite(
            Authentication authentication,
            String videoId,
            String title,
            String thumbnailUrl,
            String channelTitle) {

        User user = getLoggedInUser(authentication);

        // Prevent duplicate favorite
        if (favoriteRepository.existsByUserAndVideoId(
                user,
                videoId)) {

            return false;
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .videoId(videoId)
                .title(title)
                .thumbnailUrl(thumbnailUrl)
                .channelTitle(channelTitle)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);

        return true;
    }


    // =========================================================
    // GET USER FAVORITES
    // =========================================================

    public List<Favorite> getUserFavorites(
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        return favoriteRepository
                .findByUserOrderByCreatedAtDesc(user);
    }


    // =========================================================
    // CHECK FAVORITE
    // =========================================================

    public boolean isFavorite(
            Authentication authentication,
            String videoId) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return false;
        }

        User user = getLoggedInUser(authentication);

        return favoriteRepository
                .existsByUserAndVideoId(
                        user,
                        videoId
                );
    }


    // =========================================================
    // REMOVE FAVORITE
    // =========================================================

    @Transactional
    public void removeFavorite(
            Authentication authentication,
            Long favoriteId) {

        User user = getLoggedInUser(authentication);

        favoriteRepository.deleteByIdAndUser(
                favoriteId,
                user
        );
    }


    // =========================================================
    // REMOVE BY VIDEO ID
    // =========================================================

    @Transactional
    public void removeFavoriteByVideoId(
            Authentication authentication,
            String videoId) {

        User user = getLoggedInUser(authentication);

        favoriteRepository
                .findByUserAndVideoId(user, videoId)
                .ifPresent(favorite ->
                        favoriteRepository.delete(favorite)
                );
    }


    // =========================================================
    // COUNT FAVORITES
    // =========================================================

    public long countFavorites(
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        return favoriteRepository.countByUser(user);
    }


    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private User getLoggedInUser(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Logged-in user not found"
                        )
                );
    }
}