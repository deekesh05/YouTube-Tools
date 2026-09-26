package com.youtubetools.Controller;

import com.youtubetools.Entity.Favorite;
import com.youtubetools.Entity.History;
import com.youtubetools.Entity.User;
import com.youtubetools.Repository.UserRepository;
import com.youtubetools.Service.FavoriteService;
import com.youtubetools.Service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final HistoryService historyService;
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;


    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        User user =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );


        List<History> historyList =
                historyService.getUserHistory(
                        authentication
                );


        List<Favorite> favoriteList =
                favoriteService.getUserFavorites(
                        authentication
                );


        List<History> recentHistory =
                historyList.stream()
                        .limit(5)
                        .toList();


        List<Favorite> recentFavorites =
                favoriteList.stream()
                        .limit(5)
                        .toList();


        model.addAttribute(
                "user",
                user
        );


        model.addAttribute(
                "historyCount",
                historyList.size()
        );


        model.addAttribute(
                "favoriteCount",
                favoriteList.size()
        );


        model.addAttribute(
                "historyList",
                recentHistory
        );


        model.addAttribute(
                "favoriteList",
                recentFavorites
        );


        return "dashboard";
    }
}