package com.youtubetools.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDetails {

    private String videoUrlOrId;

    private String thumbnailUrl;

    private String title;

    private String channelTitle;

    private String publishedAt;

    private String description;

    private List<String> tags;
}