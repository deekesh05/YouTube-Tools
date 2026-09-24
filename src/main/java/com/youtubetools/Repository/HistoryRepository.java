package com.youtubetools.Repository;

import com.youtubetools.Entity.History;
import com.youtubetools.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository
        extends JpaRepository<History, Long> {

    List<History> findByUserOrderByCreatedAtDesc(User user);

    // Delete one history item only if it belongs to current user
    void deleteByIdAndUser(Long id, User user);

    // Clear all history of current user
    void deleteByUser(User user);

    // Count user's history
    long countByUser(User user);
}