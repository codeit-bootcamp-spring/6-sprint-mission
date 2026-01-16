package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentUploader {

  BinaryContent uploadBinaryContent(String fileName, byte[] bytes, String contentType);

  BinaryContent uploadBinaryContent(MultipartFile profile);
}
