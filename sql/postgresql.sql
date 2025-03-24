--- postgresql

CREATE TABLE IF NOT EXISTS "user"
(
  uid         BIGSERIAL PRIMARY KEY,
  username    VARCHAR(16)                         NOT NULL,
  password    VARCHAR(256)                        NOT NULL,
  email       VARCHAR(32)                         NULL,
  phone       VARCHAR(16)                         NULL,
  gender      INTEGER   DEFAULT 3                 NOT NULL,
  avatar      VARCHAR(256)                        NULL,
  status      INTEGER   DEFAULT 0                 NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "permission"
(
  id          BIGSERIAL PRIMARY KEY,
  uid         BIGINT                              NOT NULL,
  permission  VARCHAR(32)                         NOT NULL,
  expiry      BIGINT    DEFAULT 0                 NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "oauth"
(
  id          BIGSERIAL PRIMARY KEY,
  uid         BIGINT                              NOT NULL,
  platform    INTEGER                             NOT NULL,
  openId      VARCHAR(64)                         NOT NULL,
  token       VARCHAR(64)                         NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "log"
(
  id          BIGSERIAL PRIMARY KEY,
  uid         BIGINT                              NULL,
  request_id  VARCHAR(36)                         NOT NULL,
  ip          VARCHAR(32)                         NULL,
  headers     TEXT                                NULL,
  url         VARCHAR(256)                        NULL,
  method      VARCHAR(16)                         NULL,
  params      TEXT                                NULL,
  result      TEXT                                NULL,
  http_code   INTEGER                             NULL,
  cost        BIGINT                              NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "security_log"
(
  id          BIGSERIAL PRIMARY KEY,
  uid         BIGINT                              NULL,
  avatar      TEXT                                NULL,
  title       VARCHAR(64)                         NOT NULL,
  types       VARCHAR(36)                         NOT NULL,
  ip          VARCHAR(32)                         NULL,
  info        TEXT                                NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "orders"
(
  out_trade_no   VARCHAR(128) PRIMARY KEY,
  uid            BIGINT                              NOT NULL,
  subject        VARCHAR(128)                        NOT NULL,
  tradeNo        VARCHAR(128)                        NULL,
  total_amount   BIGINT                              NOT NULL,
  receipt_amount BIGINT                              NULL,
  pay_platform   INTEGER   DEFAULT 0                 NOT NULL,
  trade_status   INTEGER   DEFAULT 0                 NOT NULL,
  create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available      INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "blacklist"
(
  id          BIGSERIAL PRIMARY KEY,
  ip          VARCHAR(32)                         NOT NULL,
  log         BIGINT                              NOT NULL,
  reason      VARCHAR(256)                        NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "photo"
(
  pid         BIGSERIAL PRIMARY KEY,
  md5         VARCHAR(32)                         NOT NULL,
  ext         VARCHAR(16)                         NOT NULL,
  size        BIGINT                              NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "2fa"
(
  id           BIGSERIAL PRIMARY KEY,
  uid          BIGINT                              NOT NULL,
  secret       VARCHAR(512)                        NOT NULL,
  force_enable BOOLEAN   DEFAULT FALSE             NOT NULL,
  create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available    INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "invite_code"
(
  id             BIGSERIAL PRIMARY KEY,
  uid            BIGINT                              NOT NULL,
  code           VARCHAR(16)                         NOT NULL,
  expiry         BIGINT    DEFAULT 0                 NOT NULL,
  default_groups TEXT                                NULL,
  times          INTEGER   DEFAULT 0                 NOT NULL,
  create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available      INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "invite_record"
(
  id          BIGSERIAL PRIMARY KEY,
  uid         BIGINT                              NOT NULL,
  code        VARCHAR(16)                         NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
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
  status      INTEGER   DEFAULT 0                 NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 1                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "article_comment"
(
  id          BIGSERIAL PRIMARY KEY,
  uid         BIGINT                              NOT NULL,
  aid         BIGINT                              NOT NULL,
  content     TEXT                                NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "modpack"
(
  id               BIGSERIAL PRIMARY KEY,
  uid              BIGINT                              NOT NULL,
  logo_id          BIGINT                              NOT NULL,
  name             TEXT                                NULL,
  launch_arguments TEXT                                NULL,
  brief            TEXT                                NOT NULL,
  client           VARCHAR(256)                        NOT NULL,
  version          VARCHAR(64)                         NOT NULL,
  file_path        VARCHAR(256)                        NOT NULL,
  file_size        BIGINT                              NOT NULL,
  md5              VARCHAR(32)                         NOT NULL,
  status           INTEGER   DEFAULT 0                 NOT NULL,
  reason           TEXT                                NULL,
  create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available        INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "visitor"
(
  date        TIMESTAMP PRIMARY KEY,
  count       INTEGER                             NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);

CREATE TABLE IF NOT EXISTS "report"
(
  id          BIGSERIAL PRIMARY KEY,
  reporter    BIGINT                              NOT NULL,
  item_id     BIGINT                              NOT NULL,
  type        INTEGER                             NOT NULL,
  reason      TEXT                                NOT NULL,
  status      INTEGER   DEFAULT 0                 NOT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  available   INTEGER   DEFAULT 0                 NOT NULL
);
