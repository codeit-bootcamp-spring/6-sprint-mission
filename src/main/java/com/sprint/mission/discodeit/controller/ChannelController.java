package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDTO;
import com.sprint.mission.discodeit.dto.api.ErrorApiDTO;
import com.sprint.mission.discodeit.dto.api.request.ChannelRequestDTO;
import com.sprint.mission.discodeit.dto.api.request.ChannelRequestDTO.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.api.request.ChannelRequestDTO.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.api.request.ChannelRequestDTO.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.api.response.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.api.response.ChannelResponseDTO.FindChannelResponse;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.mapper.api.ChannelApiMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채널 관련 API 컨트롤러
 */
@Tag(name = "채널 API", description = "채널 생성 및 관리를 위한 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;
  private final ChannelApiMapper channelApiMapper;

  /**
   * 공개 채널 생성
   *
   * @param publicChannelCreateRequest 공개 채널 생성 요청 정보
   * @return 생성된 채널 정보
   */
  @Operation(
      summary = "공개 채널 생성",
      description = "새로운 공개 채널을 생성합니다.",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "채널 생성 성공",
              content = @Content(schema = @Schema(implementation = FindChannelResponse.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          )
      }
  )
  @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FindChannelResponse> createPublicChannel(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "공개 채널 생성 요청 정보",
          required = true,
          content = @Content(schema = @Schema(implementation = PublicChannelCreateRequest.class))
      )
      @RequestBody @Valid ChannelRequestDTO.PublicChannelCreateRequest publicChannelCreateRequest) {

    log.info("Creating public channel with name: {}", publicChannelCreateRequest.name());

    ChannelDTO.CreatePublicChannelCommand createPublicChannelCommand = ChannelDTO.CreatePublicChannelCommand.builder()
        .name(publicChannelCreateRequest.name())
        .description(publicChannelCreateRequest.description())
        .build();

    ChannelDTO.Channel channel = channelService.createChannel(createPublicChannelCommand);

    return ResponseEntity.status(201).body(ChannelResponseDTO.FindChannelResponse.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .participants(new ArrayList<>())
        .build());

  }

  /**
   * 비공개 채널 생성
   *
   * @param privateChannelCreateRequest 비공개 채널 생성 요청 정보
   * @return 생성된 채널 정보
   */
  @Operation(
      summary = "비공개 채널 생성",
      description = "새로운 비공개 채널을 생성합니다.",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "채널 생성 성공",
              content = @Content(schema = @Schema(implementation = FindChannelResponse.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          )
      }
  )
  @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FindChannelResponse> createPrivateChannel(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "비공개 채널 생성 요청 정보",
          required = true,
          content = @Content(schema = @Schema(implementation = PrivateChannelCreateRequest.class))
      )
      @RequestBody @Valid PrivateChannelCreateRequest privateChannelCreateRequest) {

    ChannelDTO.CreatePrivateChannelCommand createPrivateChannelCommand = ChannelDTO.CreatePrivateChannelCommand.builder()
        .type(ChannelType.PRIVATE)
        .participants(privateChannelCreateRequest.participantIdList())
        .description("DM")
        .build();

    ChannelDTO.Channel channel = channelService.createPrivateChannel(createPrivateChannelCommand);

    return ResponseEntity.status(201).body(channelApiMapper.toFindChannelResponse(channel));

  }

  /**
   * 채널 정보 수정
   *
   * @param channelId            채널 ID
   * @param channelUpdateRequest 채널 수정 정보
   * @return 수정된 채널 정보
   */
  @Operation(
      summary = "채널 정보 수정",
      description = "기존 채널의 정보를 수정합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "채널 수정 성공",
              content = @Content(schema = @Schema(implementation = FindChannelResponse.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "잘못된 요청",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "채널을 찾을 수 없음",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          )
      }
  )
  @PatchMapping(value = "/{channelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FindChannelResponse> updatePublicChannel(
      @Parameter(description = "채널 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
      @PathVariable @NotNull UUID channelId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "채널 수정 정보",
          required = true,
          content = @Content(schema = @Schema(implementation = ChannelUpdateRequest.class))
      )
      @RequestBody @Valid ChannelUpdateRequest channelUpdateRequest) {

    log.info("Updating public channel with id: {}", channelId);

    ChannelDTO.UpdateChannelCommand updateChannelCommand = ChannelDTO.UpdateChannelCommand.builder()
        .id(channelId)
        .name(channelUpdateRequest.name())
        .description(channelUpdateRequest.description())
        .build();

    ChannelDTO.Channel channel = channelService.updateChannel(updateChannelCommand);

    return ResponseEntity.status(200).body(channelApiMapper.toFindChannelResponse(channel));

  }

  /**
   * 채널 삭제
   *
   * @param channelId 채널 ID
   * @return 삭제 결과 메시지
   */
  @Operation(
      summary = "채널 삭제",
      description = "채널을 삭제합니다.",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "채널 삭제 성공"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "채널을 찾을 수 없음",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          )
      }
  )
  @DeleteMapping(value = "/{channelId}")
  public ResponseEntity<String> deleteChannel(
      @Parameter(description = "채널 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
      @PathVariable @NotNull UUID channelId) {

    log.info("Deleting channel with id: {}", channelId);

    channelService.deleteChannelById(channelId);

    return ResponseEntity.status(204).body("Channel deleted successfully");

  }

  /**
   * 사용자별 채널 목록 조회
   *
   * @param userId 사용자 ID
   * @return 채널 목록
   */
  @Operation(
      summary = "사용자별 채널 목록 조회",
      description = "특정 사용자가 속한 채널 목록을 조회합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "채널 목록 조회 성공",
              content = @Content(array = @ArraySchema(schema = @Schema(implementation = FindChannelResponse.class)))
          )
      }
  )
  @GetMapping()
  public List<FindChannelResponse> findChannelsByUserId(
      @Parameter(description = "사용자 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
      @RequestParam UUID userId) {

    return channelService.findChannelsByUserId(userId).stream()
        .map(channelApiMapper::toFindChannelResponse)
        .toList();

  }

}
