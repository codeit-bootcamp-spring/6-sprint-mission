package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> create(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequestDto messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequestDto> attachmentRequests = Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequestDto(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList())
                .orElse(new ArrayList<>());

        MessageResponseDto message = messageService.create(messageCreateRequest, attachmentRequests);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(message);
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageResponseDto>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId,
            @RequestParam(value = "cursor", required = false) Instant cursor, // 마지막 메시지 createdAt
            @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<MessageResponseDto> response = messageService.findAllByChannelId(channelId, cursor, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponseDto> update(@PathVariable UUID id,
                                                     @Valid @RequestBody MessageUpdateRequestDto dto) {
        MessageResponseDto message = messageService.update(id, dto);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(message);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.ok().build();
    }

}
