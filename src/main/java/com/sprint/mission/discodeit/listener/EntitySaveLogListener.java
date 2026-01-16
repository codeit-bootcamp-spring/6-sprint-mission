package com.sprint.mission.discodeit.listener;

import jakarta.persistence.PostPersist;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

@Slf4j
public class EntitySaveLogListener {

  @PostPersist
  public void afterSave(Object entity) {
    Field idField = ReflectionUtils.findField(entity.getClass(), "id");

    if (idField != null) {

      ReflectionUtils.makeAccessible(idField);

      Object id = ReflectionUtils.getField(idField, entity);
      String entityName = entity.getClass().getSimpleName();

      log.debug("[DB 저장] {} ID: {}", entityName, id);
    } else {
      log.warn("[DB 저장] {} 엔티티에 'id' 필드가 없습니다.", entity.getClass().getSimpleName());
    }
  }
}
