package com.youtubetools.Controller;

import com.youtubetools.Model.SearchVideo;
import com.youtubetools.Model.Video;
import com.youtubetools.Service.HistoryService;
import com.youtubetools.Service.YouTubeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/youtube")
public class YouTubeTagsController {

    @Autowired
    private YouTubeService youTubeService;

    @Autowired
    private HistoryService historyService;

    @Value("${youtube.api.key}")
    private String apikey;


    // Check whether YouTube API key is configured
    private boolean isApiKeyConfigured() {

        return apikey != null && !apikey.isEmpty();
    }


    @PostMapping("/search")
    public String videoTags(
            @RequestParam("videoTitle") String videoTitle,
            Model model,
            Authentication authentication) {

        // Check API key
        if (!isApiKeyConfigured()) {

            model.addAttribute(
                    "error",
                    "API key is not configured"
            );

            return "home";
        }


        // Check video title
        if (videoTitle == null ||
                videoTitle.trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Video title is required"
            );

            return "home";
        }


        try {

            // Search YouTube
            SearchVideo result =
                    youTubeService.searchVideos(videoTitle);


            // Show primary video
            model.addAttribute(
                    "primaryVideo",
                    result.getPrimaryVideo()
            );


            // Show related videos
            model.addAttribute(
                    "relatedVideos",
                    result.getRelatedVideos()
            );


            // Save history only for logged-in users
            if (authentication != null &&
                    authentication.isAuthenticated()) {

                Video primaryVideo =
                        result.getPrimaryVideo();


                if (primaryVideo != null) {

                    historyService.saveHistory(

                            authentication,

                            "SEO_TAGS",

                            primaryVideo.getId(),

                            primaryVideo.getTitle(),

                            primaryVideo.getThumbnailUrl(),

                            videoTitle
                    );
                }
            }


            return "home";


        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "home";
        }
    }
}