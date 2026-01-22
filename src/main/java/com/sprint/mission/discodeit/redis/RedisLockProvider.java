package com.sprint.mission.discodeit.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisLockProvider {

  private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
  private static final String LOCK_KEY_PREFIX = "lock:";

  private final RedisTemplate<String, Object> redisTemplate;

  public void acquireLock(String key) {
    String lockKey = LOCK_KEY_PREFIX + key;
    String lockValue = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
    ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

    // SETNX with TTL
    Boolean acquired = valueOps.setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);

    if (Boolean.TRUE.equals(acquired)) {
      log.debug("Distributed lock acquired: {} (value: {})", lockKey, lockValue);
    } else {
      log.debug("Distributed lock acquisition failed: {}", lockKey);
      throw new RedisLockAcquisitionException("Distributed lock acquisition failed: " + lockKey);
    }
  }

  public void releaseLock(String key) {
    String lockKey = LOCK_KEY_PREFIX + key;
    try {
      redisTemplate.delete(lockKey);
      log.debug("Distributed lock released: {}", lockKey);
    } catch (Exception e) {
      log.warn("Distributed lock release failed: {}", lockKey, e);
    }
  }

  public static class RedisLockAcquisitionException extends RuntimeException {

    public RedisLockAcquisitionException(String message) {
      super(message);
    }
  }
}
