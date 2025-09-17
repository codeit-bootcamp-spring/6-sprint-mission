package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface BinaryContentService {
    public void registerProfile(UUID userId, String filename);

    public void deleteProfile(UUID userId);
}
