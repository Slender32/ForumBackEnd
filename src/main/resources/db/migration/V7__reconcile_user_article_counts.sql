-- Profile articleCount follows the same visibility rules as /users/{uid}/article.
-- Correct historical drift; subsequent lifecycle changes maintain it transactionally.
UPDATE user_stats s
SET published_article_count = (
    SELECT count(*) FROM articles a
    WHERE a.author_id = s.user_id
      AND a.status = 'PUBLISHED' AND a.visibility = 'PUBLIC' AND a.deleted_at IS NULL
);
