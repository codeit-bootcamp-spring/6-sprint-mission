package com.sprint.mission.discodeit.event.topic;

public final class Topics {
    // Message
    public static final String MESSAGE_CREATED = "discodeit.MessageCreatedEvent";

    // User
    public static final String ROLE_UPDATED = "discodeit.RoleUpdatedEvent";

    // Message, User
    public static final String BINARY_CONTENT_CREATED = "discodeit.BinaryContentCreatedEvent";

    // BinaryContent
    public static final String FILE_UPLOAD_FAILED = "discodeit.FileUploadFailedEvent";

    private Topics() {}
}