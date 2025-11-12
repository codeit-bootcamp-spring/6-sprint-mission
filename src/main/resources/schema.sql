CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY NOT NULL,
    created_at   timestamp with time zone      NOT NULL,
    file_name    varchar(255)     NOT NULL,
    size         bigint           NOT NULL,
    content_type varchar(100)     NOT NULL
);

CREATE TABLE users
(
    id         uuid PRIMARY KEY NOT NULL,
    created_at timestamp with time zone      NOT NULL,
    updated_at timestamp with time zone,
    username   varchar(50)      NOT NULL UNIQUE,
    email      varchar(100)     NOT NULL UNIQUE,
    password   varchar(255)     NOT NULL,
    profile_id uuid             REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE channels
(
    id          uuid PRIMARY KEY NOT NULL,
    created_at  timestamp with time zone      NOT NULL,
    updated_at  timestamp with time zone,
    name        varchar(100),
    description varchar(500),
    type        varchar(10)      NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE messages
(
    id         uuid PRIMARY KEY NOT NULL,
    created_at timestamp with time zone      NOT NULL,
    updated_at timestamp with time zone,
    content    text,
    channel_id uuid             NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    author_id  uuid             REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE read_statuses
(
    id           uuid PRIMARY KEY NOT NULL,
    created_at   timestamp with time zone      NOT NULL,
    updated_at   timestamp with time zone,
    user_id      uuid             NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id   uuid             NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at timestamp with time zone      NOT NULL,
    UNIQUE (user_id, channel_id)
);

CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY NOT NULL,
    created_at     timestamp with time zone      NOT NULL,
    updated_at     timestamp with time zone,
    user_id        uuid             NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    last_active_at timestamp with time zone      NOT NULL,
    UNIQUE (user_id)
);

CREATE TABLE message_attachments
(
    message_id    uuid NOT NULL REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id uuid NOT NULL REFERENCES binary_contents (id) ON DELETE CASCADE,
    PRIMARY KEY (message_id, attachment_id)
);

/*
GRANT
ALL
PRIVILEGES
ON
ALL
TABLES IN SCHEMA public TO discodeit_user;

GRANT USAGE,
SELECT
ON ALL SEQUENCES IN SCHEMA public TO discodeit_user;

GRANT
EXECUTE
ON
ALL
FUNCTIONS IN SCHEMA public TO discodeit_user;*/


