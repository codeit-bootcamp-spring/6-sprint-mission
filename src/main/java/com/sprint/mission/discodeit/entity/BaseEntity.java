package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
<<<<<<< HEAD
import java.time.Instant;

public abstract class BaseEntity {
    public Instant setTime() {
        return Instant.now();
=======
public abstract class BaseEntity {
    public Long setTime() {
        return System.currentTimeMillis();
>>>>>>> 박지훈
=======
public abstract class BaseEntity {
    public Long setTime() {
        return System.currentTimeMillis();
=======
import java.time.Instant;

public abstract class BaseEntity {
    public Instant setTime() {
        return Instant.now();
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
    }
}
