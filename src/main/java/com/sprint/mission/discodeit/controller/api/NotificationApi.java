package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notification", description = "Notification API")
public interface NotificationApi {

  @Operation(summary = "알림 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "알림 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = NotificationDto.class))
      ),
      @ApiResponse(
          responseCode = "401", description = "인증되지 않은 요청",
          content = @Content(examples = @ExampleObject(value = "Unauthorized"))
      )
  })
  ResponseEntity<List<NotificationDto>> findAll();

  @Operation(summary = "알림 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "알림 단건 조회 성공",
          content = @Content(schema = @Schema(implementation = NotificationDto.class))
      ),
      @ApiResponse(
          responseCode = "401", description = "인증되지 않은 요청",
          content = @Content(examples = @ExampleObject(value = "Unauthorized"))
      ),
      @ApiResponse(
          responseCode = "403", description = "인가되지 않은 요청",
          content = @Content(examples = @ExampleObject(value = "Forbidden"))
      ),
      @ApiResponse(
          responseCode = "404", description = "알림을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Notification not found"))
      )
  })
  ResponseEntity<NotificationDto> find(
      @Parameter(description = "조회할 알림 ID") UUID notificationId
  );
}
