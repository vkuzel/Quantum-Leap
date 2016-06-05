CREATE TABLE increment (
  id         BIGSERIAL PRIMARY KEY,
  module     VARCHAR(255) NOT NULL,
  version    INT          NOT NULL,
  file_name  VARCHAR(255) NOT NULL,
  created_at TIMESTAMP    NOT NULL
);

CREATE TABLE language (
  iso_code VARCHAR(10) PRIMARY KEY,
  name     VARCHAR(50) NOT NULL
);

CREATE TABLE message (
  id       BIGSERIAL PRIMARY KEY,
  language VARCHAR(10) REFERENCES language NOT NULL,
  code     VARCHAR(255)                    NOT NULL,
  message  VARCHAR(255)                    NOT NULL
);
