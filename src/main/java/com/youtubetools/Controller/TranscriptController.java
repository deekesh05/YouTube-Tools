package com.youtubetools.Controller;

import com.youtubetools.Service.TranscriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TranscriptController {

    @Autowired
    private TranscriptService transcriptService;

    // Open transcript page
    @GetMapping("/transcript")
    public String transcriptPage() {
        return "transcript";
    }

    // Get transcript
    @PostMapping("/transcript")
    public String getTranscript(
            @RequestParam("videoUrl") String videoUrl,
            Model model) {

        try {

            String transcript =
                    transcriptService.getTranscriptFromUrl(videoUrl);

            model.addAttribute("transcript", transcript);
            model.addAttribute("videoUrl", videoUrl);

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Transcript could not be retrieved for this video."
            );

            model.addAttribute("videoUrl", videoUrl);
        }

        return "transcript";
    }
}