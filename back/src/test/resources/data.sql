INSERT INTO users (email, pseudo, password) VALUES
('alice@example.com', 'alice', '$2a$10$cm2NIg777PDZ51VBAYMkT.VRvBbPTbcpkmIAhKwMNUD8x0QExnFwO'), -- password value is password
('bob@example.com', 'bob', '$2a$10$cm2NIg777PDZ51VBAYMkT.VRvBbPTbcpkmIAhKwMNUD8x0QExnFwO');

INSERT INTO topics (name, description) VALUES
('Spring', 'Spring Boot and Spring Framework topics'),
('H2', 'In-memory H2 database testing topics');

INSERT INTO subscriptions (user_id, topic_id) VALUES
(1, 1),
(1, 2),
(2, 1);

INSERT INTO posts (user_id, topic_id, title, content, published_at) VALUES
(1, 1, 'Welcome to Spring', 'This is a welcome post for Spring.', '2026-07-14'),
(2, 1, 'Spring and H2', 'Using H2 for Spring Boot tests.', '2026-07-14'),
(2, 2, 'H2 Tutorial', 'Install and configure H2', '2026-07-14');

INSERT INTO comments (post_id, user_id, content) VALUES
(1, 2, 'Nice introduction!'),
(2, 1, 'Very helpful post!');
