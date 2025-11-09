DELETE FROM users WHERE current_timestamp > users.created_at;
DELETE FROM user_statuses WHERE current_timestamp > user_statuses.created_at;

INSERT INTO users (created_at,updated_at,id,profile_id,username,password,email) VALUES (current_timestamp,null,'33ac0646-e2a0-4f51-ac8a-1d945f0b84a1',null,'박지훈','000000','asd@asd.com');
INSERT INTO users (created_at,updated_at,id,profile_id,username,password,email) VALUES (current_timestamp,null,'0d757dfb-c7d7-4616-bc16-7e33a588186d',null,'홍길동','222222','qwe@qwe.com');
INSERT INTO user_statuses (created_at,last_access_at,updated_at,id,user_id) VALUES (current_timestamp,current_timestamp,null,'33ac2645-e2a0-4f61-ac8a-1d945f0b84a1','33ac0646-e2a0-4f51-ac8a-1d945f0b84a1');
INSERT INTO user_statuses (created_at,last_access_at,updated_at,id,user_id) VALUES (current_timestamp,current_timestamp,null,'b7a30275-26bc-45e1-acb2-d13127a21d7e','0d757dfb-c7d7-4616-bc16-7e33a588186d');
