DELETE FROM messages WHERE current_timestamp > messages.created_at;
INSERT INTO messages (created_at,updated_at,author_id,channel_id,id,content) VALUES (current_timestamp,null,'33ac0646-e2a0-4f51-ac8a-1d945f0b84a1','34ca8846-e2a0-4f51-ac8a-1d946f0b34a2','33ac0633-e2e9-4f51-ac8a-1d911f0b84a1','테스트');
