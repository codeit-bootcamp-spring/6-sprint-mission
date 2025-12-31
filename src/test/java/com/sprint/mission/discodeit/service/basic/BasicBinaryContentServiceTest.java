package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.BinaryContentDTO.BinaryContentCreateCommand;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.enums.ContentType;
import com.sprint.mission.discodeit.exception.binarycontent.NoSuchBinaryContentException;
import com.sprint.mission.discodeit.mapper.BinaryContentEntityMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicBinaryContentService 테스트")
class BasicBinaryContentServiceTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private BinaryContentEntityMapper binaryContentEntityMapper;

  @InjectMocks
  private BasicBinaryContentService basicBinaryContentService;

  private final UUID testId = UUID.randomUUID();
  private final String testFileName = "test.jpg";
  private final ContentType testContentType = ContentType.IMAGE;
  private final byte[] testData = "test data".getBytes();
  private BinaryContentDTO.BinaryContent testDto;
  private BinaryContentEntity testEntity;

  @BeforeEach
  void setUp() {

    testEntity = BinaryContentEntity.builder()
        .fileName(testFileName)
        .contentType(testContentType)
        .size((long) testData.length)
        .build();

    testDto = BinaryContentDTO.BinaryContent.builder()
        .id(testId)
        .fileName(testFileName)
        .size((long) testData.length)
        .contentType(testContentType)
        .bytes(testData)
        .build();

  }

  @Test
  @DisplayName("BinaryContent 생성 테스트")
  void createBinaryContent_Success() {

    //given
    BinaryContentCreateCommand command = new BinaryContentCreateCommand(
        testFileName, testData, testContentType);

    when(binaryContentRepository.save(any(BinaryContentEntity.class)))
        .thenReturn(testEntity);
    when(binaryContentEntityMapper.toBinaryContent(any(BinaryContentEntity.class)))
        .thenReturn(testDto);

    //when
    BinaryContentDTO.BinaryContent result = basicBinaryContentService.createBinaryContent(command);

    //then
    assertNotNull(result);
    assertEquals(testDto, result);

  }

  @Test
  @DisplayName("BinaryContent 조회 성공 테스트")
  void findBinaryContentById_Success() {

    //given
    when(binaryContentRepository.findById(testId))
        .thenReturn(Optional.of(testEntity));
    when(binaryContentStorage.get(testId))
        .thenReturn(new ByteArrayInputStream(testData));

    //when
    BinaryContentDTO.BinaryContent result = basicBinaryContentService.findBinaryContentById(testId);

    // then
    assertNotNull(result);
    assertEquals(testFileName, result.getFileName());

  }

  @Test
  @DisplayName("BinaryContent 조회 실패 테스트")
  void findBinaryContentById_Fail() {

    //given
    when(binaryContentRepository.findById(testId))
        .thenReturn(Optional.empty());

    //when & then
    assertThrows(NoSuchBinaryContentException.class, () -> {
      basicBinaryContentService.findBinaryContentById(testId);
    });

  }

  @Test
  @DisplayName("BinaryContent 다중 조회 실패 테스트")
  void findAllBinaryContentByIdIn_Fail() throws IOException {

    //given
    List<UUID> testIds = List.of(testId);

    // Make sure the entity has the testId
    testEntity = BinaryContentEntity.builder()
        .fileName(testFileName)
        .contentType(testContentType)
        .size((long) testData.length)
        .build();

    // Mock the input stream to throw IOException when read
    InputStream mockInputStream = mock(InputStream.class);
    when(mockInputStream.readAllBytes()).thenThrow(new IOException("Test IO Exception"));

    when(binaryContentRepository.findAllByIdIn(testIds))
        .thenReturn(List.of(testEntity));
    when(binaryContentStorage.get(testId))
        .thenReturn(mockInputStream);

    //when & then
    basicBinaryContentService.findAllBinaryContentByIdIn(testIds);

  }

}