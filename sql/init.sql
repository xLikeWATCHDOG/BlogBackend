--- postgresql

CREATE TABLE IF NOT EXISTS "user"
(
    uid        BIGSERIAL PRIMARY KEY,
    username   VARCHAR(16)                         NOT NULL,
    password   VARCHAR(256)                        NOT NULL,
    email      VARCHAR(32)                         NULL,
    phone      VARCHAR(16)                         NULL,
    gender     INTEGER   DEFAULT 3                 NOT NULL,
    avatar     VARCHAR(256)                        NULL,
    status     INTEGER   DEFAULT 0                 NOT NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available  BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "permission"
(
    id         BIGSERIAL PRIMARY KEY,
    uid        BIGINT                              NOT NULL,
    permission VARCHAR(32)                         NOT NULL,
    expiry     BIGINT    DEFAULT 0                 NOT NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available  BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "oauth"
(
    id         BIGSERIAL PRIMARY KEY,
    uid        BIGINT                              NOT NULL,
    platform   INTEGER                             NOT NULL,
    openId     VARCHAR(64)                         NOT NULL,
    token      VARCHAR(64)                         NOT NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available  BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "log"
(
    id         BIGSERIAL PRIMARY KEY,
    uid        BIGINT                              NULL,
    requestId  VARCHAR(36)                         NOT NULL,
    ip         VARCHAR(32)                         NULL,
    headers    TEXT                                NULL,
    url        VARCHAR(256)                        NULL,
    method     VARCHAR(16)                         NULL,
    params     TEXT                                NULL,
    result     TEXT                                NULL,
    httpCode   INTEGER                             NULL,
    cost       BIGINT                              NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available  BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "security_log"
(
    id         BIGSERIAL PRIMARY KEY,
    uid        BIGINT                              NULL,
    avatar     TEXT                                NULL,
    title      VARCHAR(64)                         NOT NULL,
    types      VARCHAR(36)                         NOT NULL,
    ip         VARCHAR(32)                         NULL,
    info       TEXT                                NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available  BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "orders"
(
    outTradeNo    VARCHAR(128) PRIMARY KEY,
    uid           BIGINT                              NOT NULL,
    subject       VARCHAR(128)                        NOT NULL,
    tradeNo       VARCHAR(128)                        NULL,
    totalAmount   BIGINT                              NOT NULL,
    receiptAmount BIGINT                              NULL,
    payPlatform   INTEGER   DEFAULT 0                 NOT NULL,
    tradeStatus   INTEGER   DEFAULT 0                 NOT NULL,
    createTime    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available     BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "blacklist"
(
    id         BIGSERIAL PRIMARY KEY,
    ip         VARCHAR(32)                         NOT NULL,
    log        BIGINT                              NOT NULL,
    reason     VARCHAR(256)                        NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available  BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "photo"
(
    pid        BIGSERIAL PRIMARY KEY,
    md5        VARCHAR(32)                         NOT NULL,
    ext        VARCHAR(16)                         NOT NULL,
    size       BIGINT                              NOT NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available  BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "2fa"
(
    id          BIGSERIAL PRIMARY KEY,
    uid         BIGINT                              NOT NULL,
    secret      VARCHAR(512)                        NOT NULL,
    forceEnable BOOLEAN   DEFAULT FALSE             NOT NULL,
    createTime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available   BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "invite_code"
(
    id            BIGSERIAL PRIMARY KEY,
    uid           BIGINT                              NOT NULL,
    code          VARCHAR(16)                         NOT NULL,
    expiry        BIGINT    DEFAULT 0                 NOT NULL,
    defaultGroups TEXT                                NULL,
    times         INTEGER   DEFAULT 0                 NOT NULL,
    createTime    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available     BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "invite_record"
(
    id         BIGSERIAL PRIMARY KEY,
    uid        BIGINT                              NOT NULL,
    code       VARCHAR(16)                         NOT NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available  BOOLEAN   DEFAULT TRUE              NOT NULL
);

CREATE TABLE IF NOT EXISTS "article"
(
    id          BIGSERIAL PRIMARY KEY,
    uid         BIGINT                              NOT NULL,
    pid         BIGINT                              NOT NULL,
    title       VARCHAR(256)                        NOT NULL,
    description TEXT                                NOT NULL,
    content     TEXT                                NOT NULL,
    tags        TEXT                                NULL,
    views       BIGINT    DEFAULT 1                 NOT NULL,
    createTime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    available   BOOLEAN   DEFAULT TRUE              NOT NULL
);