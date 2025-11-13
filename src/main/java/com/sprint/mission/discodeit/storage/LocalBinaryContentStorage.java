package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.message.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath).toAbsolutePath().normalize();
        log.info("Local storage root path initialized: {}", this.root);
    }

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
                log.info("Local storage root directory created: {}", root);
            }
        } catch (IOException e) {
            log.error("Failed to initialize local storage root directory.", e);
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    private Path resolvePath(UUID id) {
        return this.root.resolve(id.toString());
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path targetPath = resolvePath(id);
        try {
            Files.write(targetPath, bytes);
            log.debug("File saved successfully at: {}", targetPath);
            return id;
        } catch (IOException e) {
            log.error("Failed to save file for ID: {}", id, e);
            throw new RuntimeException("Failed to store file: " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path targetPath = resolvePath(id);
        try {
            return Files.newInputStream(targetPath);
        } catch (IOException e) {
            log.error("File not found or failed to read for ID: {}", id, e);
            throw new FileReadException("File not found: " + id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        try (InputStream inputStream = get(dto.id())) {

            String encodedFileName = URLEncoder.encode(dto.filename(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(dto.contentType()));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            headers.setContentLength(dto.size());

            Resource resource = new InputStreamResource(inputStream);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (FileReadException e) {
            log.warn("Attempted to download non-existent file: {}", dto.id());
            return ResponseEntity.notFound().build();

        } catch (IOException e) {
            log.error("IO Exception occurred during file download for ID: {}", dto.id(), e);
            return ResponseEntity.internalServerError().build();

        } catch (RuntimeException e) {
            log.error("An unexpected runtime error occurred during download for ID: {}", dto.id(), e);
            return ResponseEntity.internalServerError().build();
        }

    }
}