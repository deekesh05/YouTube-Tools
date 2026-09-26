package com.youtubetools.Controller;


import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import com.youtubetools.Model.VideoDetails;
import com.youtubetools.Service.FavoriteService;
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
    private final FavoriteService favoriteService;


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


            // FAVORITE STATUS

            boolean isFavorite = false;

            if (authentication != null &&
                    authentication.isAuthenticated()) {

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


            // SAVE HISTORY

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

            // Actual exception console me rahegi
            e.printStackTrace();

            // User ko technical/API details nahi dikhani
            model.addAttribute(
                    "error",
                    "Unable to fetch video data. Please try again later."
            );

            model.addAttribute(
                    "videoUrlOrId",
                    videoUrlOrId
            );

            return "video-details";
        }
    }


    // =====================================================
    // DOWNLOAD YOUTUBE THUMBNAIL
    // =====================================================

    @GetMapping("/download-thumbnail")
    public ResponseEntity<byte[]> downloadThumbnail(
            @RequestParam("videoUrlOrId") String videoUrlOrId) {

        try {

            String videoId =
                    thumbnailService.extractVideoId(videoUrlOrId);

            if (videoId == null) {
                return ResponseEntity.badRequest().build();
            }

            String thumbnailUrl =
                    "https://i.ytimg.com/vi/"
                            + videoId
                            + "/maxresdefault.jpg";

            byte[] image = WebClient.create()
                    .get()
                    .uri(thumbnailUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (image == null || image.length == 0) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.IMAGE_JPEG);

            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename(
                                    "youtube-thumbnail-"
                                            + videoId
                                            + ".jpg"
                            )
                            .build()
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(image);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        }
    }
}