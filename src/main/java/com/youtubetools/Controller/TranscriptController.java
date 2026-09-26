package com.youtubetools.Controller;

import com.youtubetools.Service.FavoriteService;
import com.youtubetools.Service.HistoryService;
import com.youtubetools.Service.TranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TranscriptController {

    private final TranscriptService transcriptService;
    private final HistoryService historyService;
    private final FavoriteService favoriteService;


    @GetMapping("/transcript")
    public String transcriptPage() {

        return "transcript";
    }


    @PostMapping("/transcript")
    public String getTranscript(

            @RequestParam("videoUrl")
            String videoUrl,

            Model model,

            Authentication authentication) {

        try {

            String transcript =
                    transcriptService.getTranscriptFromUrl(
                            videoUrl
                    );


            model.addAttribute(
                    "transcript",
                    transcript
            );


            model.addAttribute(
                    "videoUrl",
                    videoUrl
            );


            String videoId =
                    extractVideoId(videoUrl);


            model.addAttribute(
                    "videoId",
                    videoId
            );


            // =====================================================
            // FAVORITE STATUS
            // =====================================================

            boolean isFavorite = false;

            if (authentication != null &&
                    authentication.isAuthenticated() &&
                    videoId != null) {

                isFavorite =
                        favoriteService.isFavorite(
                                authentication,
                                videoId
                        );
            }


            model.addAttribute(
                    "isFavorite",
                    isFavorite
            );


            // =====================================================
            // SAVE HISTORY
            // =====================================================

            if (authentication != null &&
                    authentication.isAuthenticated()) {

                historyService.saveHistory(

                        authentication,

                        "TRANSCRIPT",

                        videoId,

                        "YouTube Transcript",

                        "https://img.youtube.com/vi/"
                                + videoId
                                + "/hqdefault.jpg",

                        videoUrl
                );
            }


        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Transcript could not be retrieved for this video."
            );

            model.addAttribute(
                    "videoUrl",
                    videoUrl
            );
        }


        return "transcript";
    }


    private String extractVideoId(String url) {

        if (url == null ||
                url.isBlank()) {

            return null;
        }


        url = url.trim();


        if (url.contains("v=")) {

            return url
                    .split("v=")[1]
                    .split("&")[0];
        }


        if (url.contains("youtu.be/")) {

            return url
                    .split("youtu.be/")[1]
                    .split("\\?")[0];
        }


        return null;
    }
}