package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

    ReadStatusDto toDto(ReadStatus readStatus);

    List<ReadStatusDto> toDtoList(List<ReadStatus> readStatuses);
}
