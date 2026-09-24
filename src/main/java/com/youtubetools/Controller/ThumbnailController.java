package com.youtubetools.Controller;

import com.youtubetools.Service.HistoryService;
import com.youtubetools.Service.ThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ThumbnailController {

    private final ThumbnailService service;
    private final HistoryService historyService;


    @GetMapping("/thumbnail")
    public String getThumbnail() {

        return "thumbnails";
    }


    @PostMapping("/get-thumbnail")
    public String showThumbnail(

            @RequestParam("videoUrlOrId")
            String videoUrlOrId,

            Model model,

            Authentication authentication) {


        String videoId =
                service.extractVideoId(
                        videoUrlOrId
                );


        if (videoId == null) {

            model.addAttribute(
                    "error",
                    "Invalid YouTube URL"
            );

            return "thumbnails";
        }


        String thumbnailUrl =
                "https://img.youtube.com/vi/"
                        + videoId
                        + "/hqdefault.jpg";


        model.addAttribute(
                "thumbnailUrl",
                thumbnailUrl
        );


        model.addAttribute(
                "videoUrlOrId",
                videoUrlOrId
        );


        // =====================================================
        // SAVE HISTORY
        // =====================================================

        if (authentication != null &&
                authentication.isAuthenticated()) {

            historyService.saveHistory(

                    authentication,

                    "THUMBNAIL",

                    videoId,

                    "YouTube Thumbnail",

                    thumbnailUrl,

                    videoUrlOrId
            );
        }


        return "thumbnails";
    }
}