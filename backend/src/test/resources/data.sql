-- Test data initialization script
INSERT INTO users (id, email, password, first_name, last_name, profile_picture, bio, is_verified, created_at, updated_at) VALUES
(1, 'test1@example.com', '$2a$10$DowJonesHash1', 'John', 'Doe', 'https://example.com/john.jpg', 'Software Developer', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'test2@example.com', '$2a$10$DowJonesHash2', 'Jane', 'Smith', 'https://example.com/jane.jpg', 'Product Manager', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'test3@example.com', '$2a$10$DowJonesHash3', 'Bob', 'Johnson', 'https://example.com/bob.jpg', 'Designer', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO posts (id, content, image_url, user_id, created_at, updated_at) VALUES
(1, 'This is a test post from John', 'https://example.com/post1.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Another test post from Jane', 'https://example.com/post2.jpg', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Test post without image', null, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO chats (id, chat_name, is_group_chat, admin_id, created_at, updated_at) VALUES
(1, 'John and Jane Chat', false, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Test Group Chat', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO chat_users (chat_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(2, 2),
(2, 3);

INSERT INTO messages (id, content, sender_id, chat_id, timestamp, is_read) VALUES
(1, 'Hello Jane!', 1, 1, CURRENT_TIMESTAMP, false),
(2, 'Hi John, how are you?', 2, 1, CURRENT_TIMESTAMP, false),
(3, 'Welcome to the group!', 1, 2, CURRENT_TIMESTAMP, false);
