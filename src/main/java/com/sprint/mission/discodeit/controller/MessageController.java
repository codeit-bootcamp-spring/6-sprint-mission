package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.Page.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/messages")
@RestController
@RequiredArgsConstructor
public class MessageController implements MessageApi {
    private final MessageService messageService;


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MessageDto> create(
            @Valid @RequestPart(name = "messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(name = "attachments", required = false) List<MultipartFile> attachments) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(attachments, messageCreateRequest));
    }


    @PatchMapping(value = "/{messageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDto> update(
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest newContent) {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.update(messageId, newContent.newContent()));
    }


    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            @RequestParam("channelId") UUID id,
            @RequestParam(name = "cursor",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cursor,
            Pageable pageable) {
        PageResponse<MessageDto> messageList = messageService.findAllByChannelId(id, cursor, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }
}
