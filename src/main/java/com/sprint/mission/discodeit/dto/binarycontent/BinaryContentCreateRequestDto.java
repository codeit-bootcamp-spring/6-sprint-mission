package com.sprint.mission.discodeit.dto.binarycontent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "파일 업로드 요청 DTO")
public record BinaryContentCreateRequestDto(

        @Size(max = 255, message = "파일명은 255자까지 입력 가능합니다.")
        @NotBlank(message = "파일명을 입력해 주세요.")
        String fileName,

        @NotBlank(message = "파일 확장자를 입력해 주세요.")
        String contentType, // 파일 확장자?

        @NotNull(message = "파일 바이트는 필수 입력값입니다.")
        byte[] bytes
) {}