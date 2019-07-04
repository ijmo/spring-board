-- user with id: a / pw: a
INSERT INTO users (authorities, email, password, username)
VALUES ('ROLE_USER', NULL, '{noop}a', 'a');

-- user with id: test / pw: test
INSERT INTO users (authorities, email, password, username)
VALUES ('ROLE_USER', NULL, '{noop}test', 'test');

INSERT INTO posts (user_id, message_id, comment_count, is_deleted, created_on)
SELECT id, NULL, 0, 0, now()
  FROM users
 WHERE username = 'test';

INSERT INTO messages (user_id, revision, title, body, is_deleted, created_on, post_id, comment_id)
SELECT u.id, 1, 'Simple made easy', 'Simple is often erroneously mistaken for easy. "Easy" means "to be at hand", "to be approachable". "Simple" is the opposite of "complex" which means "being intertwined", "being tied together". Simple != easy.', 0, now(), p.id, NULL
  FROM users u LEFT OUTER JOIN posts p ON p.user_id = u.id
 WHERE u.username = 'test';

UPDATE posts SET message_id = (SELECT m.id FROM messages m INNER JOIN users u ON u.id = m.user_id WHERE u.username = 'test');

