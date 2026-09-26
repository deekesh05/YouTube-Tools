package com.youtubetools.Controller;

import com.youtubetools.Model.SearchVideo;
import com.youtubetools.Model.Video;
import com.youtubetools.Service.FavoriteService;
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

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/youtube")
public class YouTubeTagsController {

    @Autowired
    private YouTubeService youTubeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FavoriteService favoriteService;

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
                    "Unable to generate SEO tags. Please try again later."
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


            if (authentication != null &&
                    authentication.isAuthenticated()) {

                Set<String> favoriteVideoIds =
                        new HashSet<>();

                if (result.getPrimaryVideo() != null &&
                        favoriteService.isFavorite(
                                authentication,
                                result.getPrimaryVideo().getId())) {

                    favoriteVideoIds.add(
                            result.getPrimaryVideo().getId()
                    );
                }

                if (result.getRelatedVideos() != null) {

                    result.getRelatedVideos()
                            .forEach(video -> {

                                if (favoriteService.isFavorite(
                                        authentication,
                                        video.getId())) {

                                    favoriteVideoIds.add(
                                            video.getId()
                                    );
                                }

                            });
                }

                model.addAttribute(
                        "favoriteVideoIds",
                        favoriteVideoIds
                );

            } else {

                model.addAttribute(
                        "favoriteVideoIds",
                        Set.of()
                );
            }

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

            // Actual API exception console me rahegi
            e.printStackTrace();

            // User ko technical details nahi dikhani
            model.addAttribute(
                    "error",
                    "Unable to generate SEO tags. Please try again later."
            );

            return "home";
        }
    }
}