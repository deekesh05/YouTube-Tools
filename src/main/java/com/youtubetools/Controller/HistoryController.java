package com.youtubetools.Controller;

import com.youtubetools.Service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;


    // =========================================================
    // HISTORY PAGE
    // =========================================================

    @GetMapping("/history")
    public String history(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "historyList",
                historyService.getUserHistory(authentication)
        );

        return "history";
    }


    // =========================================================
    // DELETE ONE HISTORY
    // =========================================================

    @PostMapping("/history/delete/{id}")
    public String deleteHistory(
            @PathVariable Long id,
            Authentication authentication) {

        historyService.deleteHistory(
                authentication,
                id
        );

        return "redirect:/history";
    }


    // =========================================================
    // CLEAR ALL HISTORY
    // =========================================================

    @PostMapping("/history/clear")
    public String clearAllHistory(
            Authentication authentication) {

        historyService.clearAllHistory(authentication);

        return "redirect:/history";
    }
}