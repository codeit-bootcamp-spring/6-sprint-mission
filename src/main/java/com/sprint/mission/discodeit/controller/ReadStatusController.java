package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponseDto> create(@Valid @RequestBody ReadStatusCreateRequestDto request) {
        ReadStatusResponseDto readStatus = readStatusService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(readStatus);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusResponseDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponseDto> update(@PathVariable("readStatusId") UUID readStatusId,
                                                        @Valid @RequestBody ReadStatusUpdateRequestDto request) {
        ReadStatusResponseDto readStatus = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(readStatus);
    }

}
