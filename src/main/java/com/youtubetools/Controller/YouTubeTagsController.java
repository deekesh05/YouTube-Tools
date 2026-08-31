package com.youtubetools.Controller;
import com.youtubetools.Model.SearchVideo;
import com.youtubetools.Service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${youtube.api.key}")
    private String apikey;

    private boolean isApiKeyConfigured()
    {
        return apikey!=null && !apikey.isEmpty();
    }

    @PostMapping("/search")
    public String videoTags(@RequestParam ("videoTitle") String videoTitle , Model model)
    {
        if(!isApiKeyConfigured())
        {
            model.addAttribute("error","Api key is not Configured");
            return "home";
        }
        if(videoTitle == null || videoTitle.isEmpty())
        {
            model.addAttribute("error","Video Title is Required");
            return "home";
        }
        try {
            SearchVideo result = youTubeService.searchVideos(videoTitle);
            model.addAttribute("primaryVideo",result.getPrimaryVideo());
            model.addAttribute("relatedVideos",result.getRelatedVideos());
            return "home";
        }
        catch(Exception e){
            model.addAttribute("error",e.getMessage());
            return "home";
        }
    }

}
