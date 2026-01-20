package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 없음"),
    USER_DUPLICATE_NAVE(HttpStatus.CONFLICT, "중복된 이름"),
    USER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일"),
    USER_UPDATE_FAIL(HttpStatus.NOT_FOUND, "사용자 업데이트 실패"),

    AUTH_LOGIN_FAIL(HttpStatus.NOT_FOUND, "로그인 실패"),

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없음"),
    CHANNEL_UPDATE_FAIL(HttpStatus.NOT_FOUND, "채널 업데이트 실패"),
    CHANNEL_PRIVATE_TYPE_UPDATE_NOT_POSSIBLE(HttpStatus.FORBIDDEN, "개인 채널 업데이트는 할 수 없음"),

    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없음"),

    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 상태를 찾을 수 없음"),
    USER_STATUS_DUPLICATE(HttpStatus.CONFLICT, "유저 상태 중복"),

    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "읽음 상태를 찾을 수 없음"),
    READ_STATUS_DUPLICATE(HttpStatus.CONFLICT, "읽음 상태 중복"),

    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND,"파일 정보 없음"),

    FILE_IN_PUT_FAIL(HttpStatus.BAD_REQUEST, "파일 읽어오기 실패"),
    FILE_OUT_PUT_FAIL(HttpStatus.BAD_REQUEST, "파일 저장 실패"),
    FILE_DOWNLOAD_FAIL(HttpStatus.BAD_REQUEST, "파일 다운로드 실패"),

    UNAUTHORIZED_USER(HttpStatus.FORBIDDEN,"해당 권한이 없음"),
    UNAUTHENTICATED_USER(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자"),

    INVALID_AUTH(HttpStatus.UNAUTHORIZED,"토큰이 유효하지 않습니다."),
    INVALID_USER_DETAILS(HttpStatus.UNAUTHORIZED,"사용자 인증 정보가 유효하지 않습니다."),

    LOCAL_STORAGE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Local 파일 업로드 실패"),
    S3_STORAGE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"S3 파일 업로드 실패")
    ;
    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }

}
