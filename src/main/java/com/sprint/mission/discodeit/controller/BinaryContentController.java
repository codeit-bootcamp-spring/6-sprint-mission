package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.api.ErrorApiDTO;
import com.sprint.mission.discodeit.dto.api.response.BinaryContentResponseDTO.ReadBinaryContentResponse;
import com.sprint.mission.discodeit.mapper.api.BinaryContentApiMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 바이너리 콘텐츠 관련 API 컨트롤러
 */
@Tag(name = "바이너리 콘텐츠 API", description = "바이너리 파일 업로드 및 관리를 위한 API")
@RestController
@Slf4j
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentApiMapper binaryContentApiMapper;

  /**
   * 바이너리 콘텐츠 조회
   *
   * @param id 바이너리 콘텐츠 ID
   * @return 바이너리 콘텐츠 정보
   */
  @Operation(
      summary = "바이너리 콘텐츠 조회",
      description = "지정된 ID의 바이너리 콘텐츠를 조회합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "바이너리 콘텐츠 조회 성공",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ReadBinaryContentResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "바이너리 콘텐츠를 찾을 수 없음",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          )
      }
  )
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<ReadBinaryContentResponse> readBinaryContent(
      @Parameter(description = "바이너리 콘텐츠 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
      @PathVariable("binaryContentId") @NotNull UUID id) {

    log.info("Reading binary content with ID: {}", id);

    BinaryContentDTO.BinaryContent readBinaryContentResult = binaryContentService.findBinaryContentById(
        id);

    return ResponseEntity.ok(
        binaryContentApiMapper.toReadBinaryContentResponse(readBinaryContentResult));

  }

  /**
   * 다중 바이너리 콘텐츠 조회
   *
   * @param idList 바이너리 콘텐츠 ID 목록
   * @return 바이너리 콘텐츠 목록
   */
  @Operation(
      summary = "다중 바이너리 콘텐츠 조회",
      description = "지정된 ID 목록에 해당하는 바이너리 콘텐츠 목록을 조회합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "바이너리 콘텐츠 목록 조회 성공",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  array = @ArraySchema(schema = @Schema(implementation = ReadBinaryContentResponse.class))
              )
          )
      }
  )
  @GetMapping()
  public ResponseEntity<List<ReadBinaryContentResponse>> readBinaryContentsByIdIn(
      @Parameter(
          description = "바이너리 콘텐츠 ID 목록",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000,223e4567-e89b-12d3-a456-426614174001"
      )
      @RequestParam("binaryContentIds") List<UUID> idList) {

    return ResponseEntity.ok(binaryContentService.findAllBinaryContentByIdIn(idList).stream()
        .map(binaryContentApiMapper::toReadBinaryContentResponse)
        .toList());

  }

  /**
   * 바이너리 콘텐츠 다운로드
   *
   * @param id 바이너리 콘텐츠 ID
   * @return 다운로드할 바이너리 파일
   */
  @Operation(
      summary = "바이너리 콘텐츠 다운로드",
      description = "지정된 ID의 바이너리 콘텐츠를 다운로드합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "다운로드 성공",
              content = @Content(mediaType = "application/octet-stream")
          ),
          @ApiResponse(
              responseCode = "404",
              description = "바이너리 콘텐츠를 찾을 수 없음",
              content = @Content(schema = @Schema(implementation = ErrorApiDTO.ErrorApiResponse.class))
          )
      }
  )
  @GetMapping("{binaryContentId}/download")
  public ResponseEntity<?> downloadBinaryContent(
      @Parameter(description = "바이너리 콘텐츠 ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
      @PathVariable("binaryContentId") @NotNull UUID id) {

    log.info("Downloading binary content with ID: {}", id);

    BinaryContentDTO.BinaryContent readBinaryContentResult = binaryContentService.findBinaryContentById(
        id);

    return binaryContentStorage.download(readBinaryContentResult);

  }

}
