package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface BinaryContentService {
    void registerProfile(UUID userId, String filename);

    void deleteProfile(UUID userId);
}
