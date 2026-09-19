-- V3 is an unpublished migration; only its PostgreSQL expression syntax was corrected.
-- Existing TIMESTAMP values are business wall time in Asia/Shanghai, as are new writes.
ALTER TABLE user_stats ALTER COLUMN moe_point TYPE BIGINT;
ALTER TABLE article_stats ALTER COLUMN reward_count TYPE BIGINT;
ALTER TABLE user_point_records ALTER COLUMN amount TYPE BIGINT;
DROP INDEX IF EXISTS uk_user_point_checkin;

INSERT INTO user_stats(user_id, moe_point)
SELECT u.uid, COALESCE((SELECT r.balance FROM user_point_records r WHERE r.user_id = u.uid ORDER BY r.record_id DESC LIMIT 1), 0)
FROM users u ON CONFLICT (user_id) DO NOTHING;

ALTER TABLE user_point_records ADD COLUMN idempotency_key VARCHAR(160);
UPDATE user_point_records SET idempotency_key = 'legacy:' || record_id;
ALTER TABLE user_point_records ALTER COLUMN idempotency_key SET NOT NULL;
ALTER TABLE user_point_records ADD CONSTRAINT uk_point_record_operation UNIQUE (user_id, idempotency_key);
CREATE INDEX idx_point_records_user_record ON user_point_records(user_id, record_id DESC);
ALTER TABLE user_point_records ADD CONSTRAINT fk_point_records_user FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE;

CREATE TABLE user_check_ins (
    user_id BIGINT NOT NULL REFERENCES users(uid) ON DELETE CASCADE,
    check_in_date DATE NOT NULL,
    points INTEGER NOT NULL CHECK (points BETWEEN 5 AND 20),
    consecutive_days INTEGER NOT NULL CHECK (consecutive_days > 0),
    create_time TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, check_in_date)
);

-- Keep every financial record. Select the first record per business date for
-- historical attendance; group adjacent dates to reconstruct streaks.
WITH daily AS (
    SELECT DISTINCT ON (user_id, create_time::date)
        user_id, create_time::date AS day, amount, create_time
    FROM user_point_records WHERE reason = 'CHECK_IN'
    ORDER BY user_id, create_time::date, record_id
), grouped AS (
    SELECT *, day - (row_number() OVER (PARTITION BY user_id ORDER BY day))::integer AS island
    FROM daily
)
INSERT INTO user_check_ins(user_id, check_in_date, points, consecutive_days, create_time)
SELECT user_id, day, amount::integer,
       row_number() OVER (PARTITION BY user_id, island ORDER BY day)::integer, create_time
FROM grouped;

-- V1 already rejects negative balances. Abort rather than silently alter financial history.
ALTER TABLE user_point_records ADD CONSTRAINT ck_point_record_balance CHECK (balance >= 0);
ALTER TABLE announcement_reads ADD CONSTRAINT fk_announcement_read_announcement
    FOREIGN KEY (announcement_id) REFERENCES announcements(announcement_id) ON DELETE CASCADE;
ALTER TABLE announcement_reads ADD CONSTRAINT fk_announcement_read_user
    FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE;
CREATE INDEX idx_announcement_reads_user ON announcement_reads(user_id, announcement_id);
CREATE INDEX idx_announcements_visible ON announcements(placement, is_pinned DESC, published_at DESC, announcement_id DESC)
    WHERE enabled = TRUE;
