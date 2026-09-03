package com.youtubetools.Service;

import io.github.thoroldvix.api.TranscriptApiFactory;
import io.github.thoroldvix.api.TranscriptContent;
import io.github.thoroldvix.api.TranscriptFormatters;
import io.github.thoroldvix.api.YoutubeTranscriptApi;
import io.github.thoroldvix.api.TranscriptRetrievalException;
import org.springframework.stereotype.Service;

@Service
public class TranscriptService {

    private final YoutubeTranscriptApi youtubeTranscriptApi;

    public TranscriptService() {
        youtubeTranscriptApi =
                TranscriptApiFactory.createDefault();
    }

    public String getTranscript(String videoId)
            throws TranscriptRetrievalException {

        TranscriptContent transcriptContent =
                youtubeTranscriptApi.getTranscript(videoId, "hi");

        return TranscriptFormatters.textFormatter()
                .format(transcriptContent);
    }

    private String extractVideoId(String url) {

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "YouTube URL cannot be empty"
            );
        }

        url = url.trim();

        // Normal YouTube URL
        // https://www.youtube.com/watch?v=VIDEO_ID
        if (url.contains("v=")) {

            return url
                    .split("v=")[1]
                    .split("&")[0];
        }

        // Short YouTube URL
        // https://youtu.be/VIDEO_ID?si=xxxx
        if (url.contains("youtu.be/")) {

            return url
                    .split("youtu.be/")[1]
                    .split("\\?")[0];
        }

        throw new IllegalArgumentException(
                "Invalid YouTube URL"
        );
    }

    public String getTranscriptFromUrl(String url)
            throws TranscriptRetrievalException {

        String videoId = extractVideoId(url);

        return getTranscript(videoId);
    }
}