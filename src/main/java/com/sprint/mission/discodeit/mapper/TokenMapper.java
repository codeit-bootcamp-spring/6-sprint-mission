package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.TokenDTO;
import com.sprint.mission.discodeit.entity.TokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {

  TokenDTO toToken(TokenEntity tokenEntity);

  TokenEntity toEntity(TokenDTO tokenDTO);

}
