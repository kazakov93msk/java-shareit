CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    64
) NOT NULL ,
    email VARCHAR
(
    64
) NOT NULL UNIQUE ,
    CONSTRAINT pk_user PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS items
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    64
) NOT NULL ,
    description VARCHAR
(
    512
) NOT NULL ,
    owner_id BIGINT NOT NULL ,
    is_available BOOLEAN NOT NULL ,
    request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY
(
    id
),
    CONSTRAINT fk_items_users FOREIGN KEY
(
    owner_id
) REFERENCES users
(
    id
) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    start_time
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    end_time
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    item_id
    BIGINT
    NOT
    NULL,
    booker_id
    BIGINT
    NOT
    NULL,
    status
    VARCHAR
(
    64
) NOT NULL ,
    CONSTRAINT pk_bookings PRIMARY KEY
(
    id
),
    CONSTRAINT fk_bookings_items FOREIGN KEY
(
    item_id
) REFERENCES items
(
    id
) ON DELETE CASCADE ,
    CONSTRAINT fk_bookings_users FOREIGN KEY
(
    booker_id
) REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    CONSTRAINT valid_dt_seq CHECK
(
    bookings
    .
    end_time >
    bookings
    .
    start_time
)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    description
    VARCHAR
(
    512
) NOT NULL ,
    requester_id BIGINT NOT NULL ,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW
(
),
    CONSTRAINT pk_requests PRIMARY KEY
(
    id
),
    CONSTRAINT fk_requests_users FOREIGN KEY
(
    requester_id
) REFERENCES users
(
    id
)
                      ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS comments
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    text
    VARCHAR
(
    512
) NOT NULL ,
    item_id BIGINT NOT NULL ,
    author_id BIGINT NOT NULL ,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW
(
),
    CONSTRAINT pk_comments PRIMARY KEY
(
    id
),
    CONSTRAINT fk_comments_items FOREIGN KEY
(
    item_id
) REFERENCES items
(
    id
)
                      ON DELETE CASCADE ,
    CONSTRAINT fk_comments_users FOREIGN KEY
(
    author_id
) REFERENCES users
(
    id
)
                      ON DELETE CASCADE
    );