package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/binaryContents")
@RestController
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping(value = "/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable UUID binaryContentId) {
        return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.findById(binaryContentId));
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContentDto> BinaryContentList = binaryContentIds.stream().
                map(binaryContentService::findById)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(BinaryContentList);
    }

    @GetMapping(value = "/{binaryContentId}/download")
    public ResponseEntity<?> downloadBinaryContent(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        return binaryContentStorage.download(binaryContentService.findById(binaryContentId));
    }

}
