package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateMessageRequest(
    @Size(max = 300, message = "메시지의 최대 글자 수는 300자입니다.")
    String content,
    @NotNull
    UUID channelId,
    @NotNull
    UUID authorId,
    List<@NotNull UUID> attachmentIds
) {

}
