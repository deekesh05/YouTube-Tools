package com.youtubetools.Controller;

import com.youtubetools.Model.VideoDetails;
import com.youtubetools.Service.HistoryService;
import com.youtubetools.Service.ThumbnailService;
import com.youtubetools.Service.YouTubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YouTubeVideoController {

    private final YouTubeService youTubeService;
    private final ThumbnailService thumbnailService;
    private final HistoryService historyService;


    @GetMapping("/video-details")
    public String videoDetails() {

        return "video-details";
    }


    @PostMapping("/video-details")
    public String getVideoDetails(
            @RequestParam("videoUrlOrId") String videoUrlOrId,
            Model model,
            Authentication authentication) {

        try {

            if (videoUrlOrId == null ||
                    videoUrlOrId.isBlank()) {

                model.addAttribute(
                        "error",
                        "YouTube Video URL or ID is required"
                );

                return "video-details";
            }


            String videoId =
                    thumbnailService.extractVideoId(
                            videoUrlOrId
                    );


            if (videoId == null) {

                model.addAttribute(
                        "error",
                        "Invalid YouTube Video URL or ID"
                );

                model.addAttribute(
                        "videoUrlOrId",
                        videoUrlOrId
                );

                return "video-details";
            }


            VideoDetails videoDetails =
                    youTubeService.getVideoDetails(videoId);


            if (videoDetails == null) {

                model.addAttribute(
                        "error",
                        "Video not found"
                );

                model.addAttribute(
                        "videoUrlOrId",
                        videoUrlOrId
                );

                return "video-details";
            }


            videoDetails.setVideoUrlOrId(
                    videoUrlOrId
            );


            model.addAttribute(
                    "videoDetails",
                    videoDetails
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

                        "VIDEO_DETAILS",

                        videoId,

                        videoDetails.getTitle(),

                        videoDetails.getThumbnailUrl(),

                        videoUrlOrId
                );
            }


            return "video-details";


        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Unable to fetch video details: "
                            + e.getMessage()
            );

            model.addAttribute(
                    "videoUrlOrId",
                    videoUrlOrId
            );

            return "video-details";
        }
    }
}