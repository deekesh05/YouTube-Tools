package com.youtubetools.Service;
import com.youtubetools.Model.VideoDetails;
import com.youtubetools.Model.SearchVideo;
import com.youtubetools.Model.Video;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YouTubeService {

   private final WebClient.Builder webClientBuilder;

   @Value("${youtube.api.key}")
   private String apiKey;

   @Value("${youtube.api.base.url}")
   private String youTubeUrl;

   @Value("${youtube.api.max.related.videos}")
   private int maxRelatedVideos;

   public SearchVideo searchVideos(String videoTitle) {

      List<String> videoIds = searchForVideoIds(videoTitle);

      if(videoIds.isEmpty()) {
         return SearchVideo.builder()
                 .primaryVideo(null)
                 .relatedVideos(Collections.emptyList())
                 .build();
      }

      String primaryVideoId = videoIds.get(0);
      List<String> relatedVideoIds = videoIds.subList(1,Math.min(videoIds.size(),maxRelatedVideos+1));
      Video primaryVideo = getVideoById(primaryVideoId);
      List<Video> relatedVideos = new ArrayList<>();
      for(String id : relatedVideoIds)
      {
         Video video = getVideoById(id);
         if(video!=null)
         {
            relatedVideos.add(video);
         }
      }
      return SearchVideo
              .builder()
              .primaryVideo(primaryVideo)
              .relatedVideos(relatedVideos)
              .build();

   }

   private List<String> searchForVideoIds(String videoTitle) {

      SearchApiResponse response = webClientBuilder.baseUrl(youTubeUrl).build()
              .get()
              .uri(uriBuilder -> uriBuilder
                      .path("/search")
                      .queryParam("part","snippet")
                      .queryParam("q" , videoTitle)
                      .queryParam("type","video")
                      .queryParam("maxResults",maxRelatedVideos)
                      .queryParam("key",apiKey)
                      .build())
              .retrieve()
              .bodyToMono(SearchApiResponse.class)
              .block();

      if(response == null || response.items==null)
      {
         return Collections.emptyList();
      }
        List<String> videoIds = new ArrayList<>();
         for(SearchItem item : response.items)
         {
            videoIds.add(item.id.videoId);
         }
         return videoIds;
   }

   private Video getVideoById(String videoId) {
      VideoApiResponse response = webClientBuilder.baseUrl(youTubeUrl).build()
              .get()
              .uri(uriBuilder -> uriBuilder
                      .path("/videos")
                      .queryParam("part","snippet")
                      .queryParam("id",videoId)
                      .queryParam("key",apiKey)
                      .build())
              .retrieve()
              .bodyToMono(VideoApiResponse.class)
              .block();

      if(response==null || response.items==null)
      {
         return null;
      }
      Snippet snippet = response.items.get(0).snippet;
      return Video.builder()
              .id(videoId)
              .channelTitle(snippet.channelTitle)
              .title(snippet.title)
              .tags(snippet.tags == null ? Collections.emptyList() :snippet.tags)
              .build();

   }

   public VideoDetails getVideoDetails(String videoId) {

      VideoApiResponse response = webClientBuilder
              .baseUrl(youTubeUrl)
              .build()
              .get()
              .uri(uriBuilder -> uriBuilder
                      .path("/videos")
                      .queryParam("part", "snippet")
                      .queryParam("id", videoId)
                      .queryParam("key", apiKey)
                      .build())
              .retrieve()
              .bodyToMono(VideoApiResponse.class)
              .block();

      if (response == null ||
              response.items == null ||
              response.items.isEmpty()) {

         return null;
      }

      Snippet snippet = response.items.get(0).snippet;

      String thumbnailUrl =
              "https://i.ytimg.com/vi/" + videoId + "/maxresdefault.jpg";

      return VideoDetails.builder()
              .videoUrlOrId(videoId)
              .thumbnailUrl(thumbnailUrl)
              .title(snippet.title)
              .channelTitle(snippet.channelTitle)
              .publishedAt(snippet.publishedAt)
              .description(snippet.description)
              .tags(
                      snippet.tags == null
                              ? Collections.emptyList()
                              : snippet.tags
              )
              .build();
   }

   @Data
   static class SearchApiResponse {
      private List<SearchItem> items;
   }

   @Data
   static class SearchItem {
      private VideoId id;
   }

   @Data
   static class VideoId {
      private String videoId;
   }

   @Data
   static class VideoApiResponse {
      private List<VideoItem> items;
   }

   @Data
   static class VideoItem {
      private Snippet snippet;
   }

   @Data
   static class Snippet {
      private String title;

      private String channelTitle;

      private String publishedAt;

      private String description;

      private List<String> tags;
   }

}
