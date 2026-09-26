package com.youtubetools.Controller;

import com.youtubetools.Service.FavoriteService;
import com.youtubetools.Service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SavedDataController {

    private final HistoryService historyService;
    private final FavoriteService favoriteService;


    @GetMapping("/saved-data")
    public String savedData(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "historyList",
                historyService.getUserHistory(
                        authentication
                )
        );


        model.addAttribute(
                "favoriteList",
                favoriteService.getUserFavorites(
                        authentication
                )
        );


        return "saved-data";
    }
}