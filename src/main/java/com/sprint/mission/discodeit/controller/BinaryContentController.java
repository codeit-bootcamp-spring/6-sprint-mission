package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.BinaryContentService;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/binary-content")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @PostMapping("/profile/{userId}")
    public void registerProfile(@PathVariable UUID userId, @RequestParam("fileName") String fileName) {
        binaryContentService.registerProfile(userId, fileName);
    }

    @DeleteMapping("/profile/{userId}")
    public void deleteProfile(@PathVariable UUID userId) {
        binaryContentService.deleteProfile(userId);
    }
}