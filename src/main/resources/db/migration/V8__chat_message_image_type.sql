ALTER TABLE messages
    ADD COLUMN message_type VARCHAR(16) NOT NULL DEFAULT 'TEXT';
ALTER TABLE messages
    ADD CONSTRAINT messages_message_type_check CHECK (message_type IN ('TEXT', 'IMAGE'));
