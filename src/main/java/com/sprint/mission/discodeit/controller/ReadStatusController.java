package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.message.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/read-statuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @Autowired
    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @PostMapping
    public ReadStatus createReadStatus(@RequestBody ReadStatusCreateDto dto) {
        return readStatusService.create(dto);
    }

    @GetMapping("/{id}")
    public ReadStatus findReadStatusById(@PathVariable UUID id) {
        return readStatusService.find(id);
    }

    @GetMapping("/user/{userId}")
    public List<ReadStatus> findAllByUserId(@PathVariable UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    @PutMapping("/{id}")
    public ReadStatus updateReadStatus(@PathVariable UUID id, @RequestBody ReadStatusUpdateDto dto) {
        return readStatusService.update(id, dto);
    }

    @DeleteMapping("/user/{userId}/channel/{channelId}")
    public void deleteReadStatus(@PathVariable UUID userId, @PathVariable UUID channelId) {
        readStatusService.delete(userId, channelId);
    }
}