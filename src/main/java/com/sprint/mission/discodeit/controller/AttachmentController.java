package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.BinaryContentDto;
import com.sprint.mission.discodeit.service.AttachmentService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/{messageId}")
    public void saveAttachments(@PathVariable UUID messageId, @RequestBody List<BinaryContentDto> attachments) {
        attachmentService.saveAttachments(messageId, attachments);
    }

    @GetMapping("/{messageId}")
    public List<BinaryContentDto> getAttachmentsByMessageId(@PathVariable UUID messageId) {
        return attachmentService.findAllByMessageId(messageId);
    }

    @GetMapping("/{messageId}/{filename}")
    public Optional<BinaryContentDto> getAttachmentByMessageIdAndFilename(@PathVariable UUID messageId, @PathVariable String filename) {
        return attachmentService.findByMessageIdAndFilename(messageId, filename);
    }
}
