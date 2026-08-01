ALTER TABLE users
    ADD COLUMN level INTEGER NOT NULL DEFAULT 0,
    ADD CONSTRAINT ck_users_level CHECK (level >= 0 AND level <= 6);
