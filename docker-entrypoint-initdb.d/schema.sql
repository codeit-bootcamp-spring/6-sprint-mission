-- users
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP
);

-- channels
CREATE TABLE channels (
                          id UUID PRIMARY KEY,
                          type VARCHAR(50) NOT NULL,
                          name VARCHAR(255),
                          description VARCHAR(255),
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP
);

-- messages
CREATE TABLE messages (
                          id UUID PRIMARY KEY,
                          user_id UUID,
                          channel_id UUID,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP,
                          CONSTRAINT fk_message_user FOREIGN KEY (user_id) REFERENCES users(id),
                          CONSTRAINT fk_message_channel FOREIGN KEY (channel_id) REFERENCES channels(id)
);

-- read_statuses
CREATE TABLE read_statuses (
                               id UUID PRIMARY KEY,
                               user_id UUID,
                               channel_id UUID,
                               last_read_at TIMESTAMP,
                               notification_enabled boolean NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP,
                               CONSTRAINT fk_read_user FOREIGN KEY (user_id) REFERENCES users(id),
                               CONSTRAINT fk_read_channel FOREIGN KEY (channel_id) REFERENCES channels(id)
);

-- binary_contents
CREATE TABLE binary_contents (
                                 id UUID PRIMARY KEY,
                                 message_id UUID,
                                 user_id UUID,
                                 content_type VARCHAR(50) NOT NULL,
                                 file_name VARCHAR(255) NOT NULL,
                                 size BIGINT NOT NULL,
                                 status varchar(20) NOT NULL,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP,
                                 CONSTRAINT fk_binary_message FOREIGN KEY (message_id) REFERENCES messages(id),
                                 CONSTRAINT fk_binary_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- notifications
CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               user_id UUID,
                               title VARCHAR(255) NOT NULL,
                               content VARCHAR(255) NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               CONSTRAINT fk_binary_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX idx_users_username ON users(username);
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE INDEX idx_messages_created_at ON messages(created_at);