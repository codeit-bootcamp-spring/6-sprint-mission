package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  @Named("binaryContentToDto")
  BinaryContentDto toDto(BinaryContent binaryContent);

  @Named("binaryContentToDtoList")
  List<BinaryContentDto> toDtoList(List<BinaryContent> binaryContentList);
}
