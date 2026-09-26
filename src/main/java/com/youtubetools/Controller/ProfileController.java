package com.youtubetools.Controller;

import com.youtubetools.Entity.User;
import com.youtubetools.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;


    @GetMapping("/profile")
    public String profile(
            Authentication authentication,
            Model model) {

        User user =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );


        model.addAttribute(
                "user",
                user
        );


        return "profile";
    }


    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String name,
            Authentication authentication) {

        User user =
                userRepository
                        .findByEmail(authentication.getName())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );


        if (name != null &&
                !name.isBlank()) {

            user.setName(
                    name.trim()
            );

            userRepository.save(user);
        }


        return "redirect:/profile?updated";
    }
}