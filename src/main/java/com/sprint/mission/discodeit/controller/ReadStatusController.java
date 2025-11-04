package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 스프린트5 프론트엔드에서 url 표기 방식을 CamelCase로 사용 중
// `${Xe.apiBaseUrl}/readStatuses`
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  public final ReadStatusService readStatusService;
  public final ReadStatusMapper readStatusMapper;

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> readStatusByUserId(
      @RequestParam UUID userId
  ) {
    List<ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(readStatusMapper.toDtoList(readStatusList));
  }

  @PostMapping
  public ResponseEntity<ReadStatusDto> createReadStatus(
      @Valid @RequestBody CreateReadStatusRequest request
  ) {
    ReadStatus readStatus = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatusMapper.toDto(readStatus));
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> updateReadStatus(
      @PathVariable UUID readStatusId,
      @Valid @RequestBody UpdateReadStatusRequest request
  ) {
    ReadStatus updated = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(readStatusMapper.toDto(updated));
  }

}
