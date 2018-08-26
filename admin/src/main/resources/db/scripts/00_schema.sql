CREATE SCHEMA admin;

CREATE TABLE admin.notification (
  id                BIGSERIAL PRIMARY KEY,
  code              VARCHAR    NOT NULL,
  message_arguments VARCHAR [] NOT NULL,
  person_id         BIGINT REFERENCES core.person,
  role_id           BIGINT REFERENCES core.role,
  created_at        TIMESTAMP  NOT NULL,
  resolved_at       TIMESTAMP,
  resolved_by       BIGINT REFERENCES core.person
);

CREATE INDEX index_unresolved_notifications
  ON admin.notification ((resolved_at IS NULL));
