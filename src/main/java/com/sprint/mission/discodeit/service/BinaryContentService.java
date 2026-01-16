package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.model.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {

  BinaryContent create(CreateBinaryContentRequest request);

  BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status);

  BinaryContent find(UUID binaryContentId);

  List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIdList);

  void delete(UUID binaryContentId);
}
