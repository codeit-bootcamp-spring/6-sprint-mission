package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage storage;

  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> readBinaryContent(
      @PathVariable UUID binaryContentId
  ) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok(binaryContentMapper.toDto(binaryContent));
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> readBinaryContents(
      @RequestParam List<UUID> binaryContentIds
  ) {
    List<BinaryContent> binaryContentList = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok(binaryContentMapper.toDtoList(binaryContentList));
  }

  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> downloadBinaryContent(
      @PathVariable UUID binaryContentId
  ) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return storage.download(binaryContentMapper.toDto(binaryContent));
  }
}
