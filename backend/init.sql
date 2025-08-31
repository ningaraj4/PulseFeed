-- Sample data for PulseFeed application

-- Insert sample users
INSERT INTO users (username, email, password_hash, full_name, bio, avatar, is_verified) VALUES
('john_doe', 'john@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Doe', 'Software developer and tech enthusiast', 'https://via.placeholder.com/150', false),
('jane_smith', 'jane@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane Smith', 'Designer and creative thinker', 'https://via.placeholder.com/150', true),
('mike_wilson', 'mike@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Mike Wilson', 'Product manager at tech startup', 'https://via.placeholder.com/150', false),
('sarah_johnson', 'sarah@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sarah Johnson', 'Marketing specialist and content creator', 'https://via.placeholder.com/150', true),
('alex_brown', 'alex@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Alex Brown', 'Full-stack developer', 'https://via.placeholder.com/150', false);

-- Insert sample follows
INSERT INTO follows (follower_id, following_id) VALUES
(1, 2), (1, 3), (1, 4),
(2, 1), (2, 3), (2, 5),
(3, 1), (3, 2), (3, 4), (3, 5),
(4, 1), (4, 2), (4, 3),
(5, 1), (5, 2), (5, 3), (5, 4);

-- Insert sample posts
INSERT INTO posts (user_id, content, media_urls, media_type, likes_count, comments_count) VALUES
(1, 'Just finished building a new React component! 🚀 #coding #react', '[]', '', 5, 2),
(2, 'Beautiful sunset from my office window today 🌅', '["https://via.placeholder.com/400x300"]', 'image', 12, 4),
(3, 'Excited to announce our new product launch! Stay tuned for updates 📱', '[]', '', 8, 3),
(4, 'Coffee and code - the perfect combination ☕️💻', '["https://via.placeholder.com/400x300"]', 'image', 15, 6),
(5, 'Working on some exciting new features. Can''t wait to share them with you all! #development', '[]', '', 7, 1),
(1, 'Learning Go has been an amazing journey. The concurrency model is fantastic! #golang', '[]', '', 9, 3),
(2, 'Design inspiration from nature 🌿 Always finding beauty in simple things', '["https://via.placeholder.com/400x300"]', 'image', 18, 5),
(3, 'Team meeting went great today. Love working with such talented people! 👥', '[]', '', 6, 2),
(4, 'New blog post is live! Check out my thoughts on modern web development trends', '[]', '', 11, 4),
(5, 'Debugging session complete. Sometimes the smallest bugs teach you the most 🐛', '[]', '', 4, 1);

-- Insert sample likes
INSERT INTO likes (post_id, user_id) VALUES
(1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 3), (2, 4), (2, 5),
(3, 1), (3, 2), (3, 4), (3, 5),
(4, 1), (4, 2), (4, 3), (4, 5),
(5, 1), (5, 2), (5, 3), (5, 4),
(6, 2), (6, 3), (6, 4), (6, 5),
(7, 1), (7, 3), (7, 4), (7, 5),
(8, 1), (8, 2), (8, 4), (8, 5),
(9, 1), (9, 2), (9, 3), (9, 5),
(10, 1), (10, 2), (10, 3), (10, 4);

-- Insert sample comments
INSERT INTO comments (post_id, user_id, content) VALUES
(1, 2, 'Great work! React components are so powerful'),
(1, 3, 'Would love to see the code if you can share it'),
(2, 1, 'Stunning view! Where is this?'),
(2, 3, 'Nature photography at its finest'),
(2, 4, 'This makes me want to travel'),
(2, 5, 'Beautiful capture!'),
(3, 1, 'Congratulations! Looking forward to it'),
(3, 2, 'Can''t wait to try it out'),
(3, 4, 'Exciting times ahead!'),
(4, 1, 'The developer''s fuel! ☕️'),
(4, 2, 'My daily routine too'),
(4, 3, 'Coffee makes everything better'),
(4, 5, 'Perfect combination indeed'),
(4, 4, 'What''s your favorite coffee blend?'),
(4, 1, 'I prefer dark roast in the morning'),
(5, 2, 'Can''t wait to see what you''re building!'),
(6, 3, 'Go is such an elegant language'),
(6, 4, 'The goroutines are game-changing'),
(6, 5, 'Have you tried the new generics features?'),
(7, 1, 'Your designs are always inspiring'),
(7, 3, 'Nature is the best teacher'),
(7, 4, 'Simple yet profound'),
(7, 5, 'Love your aesthetic'),
(7, 2, 'Thank you all for the kind words!'),
(8, 1, 'Team collaboration is everything'),
(8, 2, 'You''re lucky to have such a great team'),
(9, 1, 'Just read it - excellent insights!'),
(9, 2, 'Shared it with my team'),
(9, 3, 'Very informative, thanks for sharing'),
(9, 5, 'Looking forward to your next post'),
(10, 2, 'The debugging struggle is real');

-- Insert sample notifications
INSERT INTO notifications (user_id, type, actor_id, post_id) VALUES
(1, 'like', 2, 1),
(1, 'comment', 2, 1),
(1, 'follow', 2, NULL),
(2, 'like', 1, 2),
(2, 'comment', 1, 2),
(3, 'like', 1, 3),
(3, 'comment', 1, 3),
(4, 'like', 1, 4),
(4, 'comment', 1, 4),
(5, 'like', 1, 5),
(5, 'follow', 1, NULL);
