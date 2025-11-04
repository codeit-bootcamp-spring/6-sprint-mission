package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;
  private final MessageMapper messageMapper;
  private final PageResponseMapper<MessageDto> pageResponseMapper;

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor,
      @PageableDefault(sort = "createdAt", direction = Direction.DESC, size = 50) Pageable pageable
  ) {
    Slice<MessageDto> dtoSlice = messageService.findAllByChannelId(channelId, cursor, pageable)
        .map(messageMapper::toDto);
    return ResponseEntity.ok(pageResponseMapper.fromSlice(dtoSlice));
  }

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
      MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<MessageDto> createMessage(
      @Valid @RequestPart("messageCreateRequest") CreateMessageRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    Message message = messageService.create(request, attachments);
    return ResponseEntity.status(HttpStatus.CREATED).body(messageMapper.toDto(message));
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> updateMessage(
      @PathVariable UUID messageId,
      @Valid @RequestBody UpdateMessageRequest request
  ) {
    Message message = messageService.update(messageId, request);
    return ResponseEntity.ok(messageMapper.toDto(message));
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<String> deleteChannel(
      @PathVariable UUID messageId
  ) {
    messageService.delete(messageId);
    return ResponseEntity.ok(messageId + " 삭제되었습니다");
  }

}
