-- 1. 외래 키(FK) 검사를 잠시 끕니다. (이것이 핵심입니다)
SET REFERENTIAL_INTEGRITY FALSE;

-- 2. 모든 테이블의 데이터를 순서 상관없이 강제로 삭제합니다.
-- (🚨 프로젝트의 모든 테이블명을 적어주세요)
TRUNCATE TABLE message_attachments;
TRUNCATE TABLE messages;
TRUNCATE TABLE channels;
TRUNCATE TABLE users;
TRUNCATE TABLE user_statuses;
-- (다른 모든 테이블...)

-- 3. 외래 키(FK) 검사를 다시 켭니다.
SET REFERENTIAL_INTEGRITY TRUE;