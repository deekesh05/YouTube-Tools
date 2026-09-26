package com.youtubetools.Controller;

import com.youtubetools.Service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;


    // =========================================================
    // FAVORITES PAGE
    // =========================================================

    @GetMapping("/favorites")
    public String favorites(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "favoriteList",
                favoriteService.getUserFavorites(authentication)
        );

        return "favorites";
    }


    // =========================================================
    // ADD FAVORITE
    // =========================================================

    @PostMapping("/favorites/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addFavorite(

            @RequestParam String videoId,

            @RequestParam(required = false)
            String title,

            @RequestParam(required = false)
            String thumbnailUrl,

            @RequestParam(required = false)
            String channelTitle,

            Authentication authentication) {

        boolean added = favoriteService.addFavorite(
                authentication,
                videoId,
                title,
                thumbnailUrl,
                channelTitle
        );

        if (added) {

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Added to favorites"
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", false,
                        "message", "Already in favorites"
                )
        );
    }


    // =========================================================
    // REMOVE FAVORITE
    // =========================================================

    @PostMapping("/favorites/remove/{id}")
    public String removeFavorite(
            @PathVariable Long id,
            Authentication authentication) {

        favoriteService.removeFavorite(
                authentication,
                id
        );

        return "redirect:/favorites";
    }
}