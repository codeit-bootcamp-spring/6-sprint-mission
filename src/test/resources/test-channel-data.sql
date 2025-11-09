DELETE FROM messages WHERE current_timestamp > messages.created_at;
DELETE FROM channels WHERE current_timestamp > channels.created_at;
INSERT INTO channels (created_at,updated_at,type,id,name,description) VALUES (current_timestamp,null,'PUBLIC','34ca8846-e2a0-4f51-ac8a-1d946f0b34a2','공게 체널 테스트','테스트');
