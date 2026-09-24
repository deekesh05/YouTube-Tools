package com.youtubetools.Service;

import com.youtubetools.Entity.History;
import com.youtubetools.Entity.User;
import com.youtubetools.Repository.HistoryRepository;
import com.youtubetools.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;


    // =========================================================
    // SAVE HISTORY
    // =========================================================

    public void saveHistory(
            Authentication authentication,
            String toolType,
            String videoId,
            String title,
            String thumbnailUrl,
            String input) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return;
        }

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Logged-in user not found"
                        )
                );

        History history = History.builder()
                .user(user)
                .toolType(toolType)
                .videoId(videoId)
                .title(title)
                .thumbnailUrl(thumbnailUrl)
                .input(input)
                .createdAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }


    // =========================================================
    // GET USER HISTORY
    // =========================================================

    public List<History> getUserHistory(
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        return historyRepository
                .findByUserOrderByCreatedAtDesc(user);
    }


    // =========================================================
    // DELETE ONE HISTORY
    // =========================================================

    @Transactional
    public void deleteHistory(
            Authentication authentication,
            Long historyId) {

        User user = getLoggedInUser(authentication);

        historyRepository.deleteByIdAndUser(
                historyId,
                user
        );
    }


    // =========================================================
    // CLEAR ALL HISTORY
    // =========================================================

    @Transactional
    public void clearAllHistory(
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        historyRepository.deleteByUser(user);
    }


    // =========================================================
    // COUNT HISTORY
    // =========================================================

    public long countHistory(
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        return historyRepository.countByUser(user);
    }


    // =========================================================
    // GET CURRENT USER
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