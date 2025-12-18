package com.sprint.mission.discodeit.mapper.api;

import com.sprint.mission.discodeit.dto.BinaryContentDTO.BinaryContent;
import com.sprint.mission.discodeit.dto.api.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.dto.api.response.BinaryContentResponseDTO.ReadBinaryContentResponse;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentApiMapper {

  public ReadBinaryContentResponse toReadBinaryContentResponse(BinaryContent binaryContent) {

    return BinaryContentResponseDTO.ReadBinaryContentResponse.builder()
        .id(binaryContent.getId())
        .fileName(binaryContent.getFileName())
        .size(binaryContent.getSize())
        .contentType(binaryContent.getContentType())
        .build();

  }

}
