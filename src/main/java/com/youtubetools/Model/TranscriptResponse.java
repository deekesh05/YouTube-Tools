package com.youtubetools.Model;

public class TranscriptResponse {

    private String videoId;
    private String language;
    private String transcript;

    public TranscriptResponse(String videoId, String language, String transcript) {
        this.videoId = videoId;
        this.language = language;
        this.transcript = transcript;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getLanguage() {
        return language;
    }

    public String getTranscript() {
        return transcript;
    }
}