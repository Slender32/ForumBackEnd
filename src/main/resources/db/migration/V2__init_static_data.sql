-- Seed roles, permissions, and public website content.

INSERT INTO roles (code, name, description) VALUES
    ('ADMIN', 'Administrator', 'Full system administrator'),
    ('MODERATOR', 'Moderator', 'Forum content moderator'),
    ('USER', 'User', 'Registered forum user');

INSERT INTO permissions (code, name, resource, action, description) VALUES
    ('article:create', 'Create article', 'article', 'create', 'Create own articles'),
    ('article:update:own', 'Update own article', 'article', 'update_own', 'Update own articles'),
    ('article:update:any', 'Update any article', 'article', 'update_any', 'Update any article'),
    ('article:delete:own', 'Delete own article', 'article', 'delete_own', 'Delete own articles'),
    ('article:delete:any', 'Delete any article', 'article', 'delete_any', 'Delete any article'),
    ('article:audit', 'Audit article', 'article', 'audit', 'Audit article publication'),
    ('article:manage', 'Manage articles', 'article', 'manage', 'Edit and remove any article'),
    ('comment:create', 'Create comment', 'comment', 'create', 'Create comments'),
    ('comment:delete:own', 'Delete own comment', 'comment', 'delete_own', 'Delete own comments'),
    ('comment:delete:any', 'Delete any comment', 'comment', 'delete_any', 'Delete any comment'),
    ('comment:manage', 'Manage comments', 'comment', 'manage', 'Edit and remove any comment'),
    ('user:ban', 'Ban user', 'user', 'ban', 'Ban or unban users'),
    ('user:assign-role', 'Assign role', 'user', 'assign_role', 'Assign roles to users'),
    ('system:manage', 'Manage system', 'system', 'manage', 'Manage system settings'),
    ('report:manage', 'Manage reports', 'report', 'manage', 'Review and archive reports'),
    ('carousel:manage', 'Manage carousels', 'carousel', 'manage', 'Create, edit and remove carousels'),
    ('role:manage', 'Manage roles', 'role', 'manage', 'Manage user role bindings'),
    ('tag:manage', 'Manage tags', 'tag', 'manage', 'Manage tags and tag relations'),
    ('relation:manage', 'Manage relations', 'relation', 'manage', 'Manage user and reaction relations'),
    ('website:manage', 'Manage website data', 'website', 'manage', 'Manage website introductions and releases'),
    ('sensitive-word:manage', 'Manage sensitive words', 'sensitive-word', 'manage', 'Manage sensitive word rules'),
    ('archive:read', 'Read archive records', 'archive', 'read', 'Read archive metadata and retry failed archives'),
    ('audit:read', 'Read audit logs', 'audit', 'read', 'Read administrative audit logs'),
    ('content-review:manage', 'Manage content reviews', 'content-review', 'manage', 'Review pending content');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'ADMIN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
JOIN permissions p ON p.code IN (
    'article:update:any',
    'article:delete:any',
    'article:audit',
    'article:manage',
    'comment:delete:any',
    'comment:manage',
    'user:ban',
    'report:manage',
    'carousel:manage',
    'content-review:manage'
)
WHERE r.code = 'MODERATOR';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
JOIN permissions p ON p.code IN (
    'article:create',
    'article:update:own',
    'article:delete:own',
    'comment:create',
    'comment:delete:own'
)
WHERE r.code = 'USER';

INSERT INTO website_introductions (title, description, image_url, sort_order) VALUES
    ('发现你感兴趣的内容', '浏览社区中的精彩内容，快速找到与你兴趣相投的话题。', 'https://i1.hdslb.com/bfs/archive/64af2fc5d243b6f381b92bca6d4395a9519c5757.jpg', 1),
    ('随时加入交流讨论', '参与文章评论与社区互动，让每一次交流都自然流畅。', 'https://i1.hdslb.com/bfs/archive/010b67496563c29fd8cff150092bd4c7f49f8741.jpg', 2),
    ('分享你的想法与经验', '通过清晰易用的编辑体验，记录并分享值得被看见的内容。', 'https://i1.hdslb.com/bfs/archive/fc380e9c81577d21827ad52b5f3d103fc1109968.jpg', 3),
    ('在不同设备保持连接', '面向 Windows 与 Android 平台，随时回到你关注的社区。', 'https://i1.hdslb.com/bfs/archive/63b52d9d314c3dd90c9bf239808dcf1ebf0bbe3c.jpg', 4);

INSERT INTO website_releases (platform, version, title, release_notes, sha256, download_url, release_date) VALUES
    ('Windows', '1.0.0', '', '', '8f8c49eacb4fb47e7d4bb24df1c814ef39d90d52bd5eb349af6c84d47a65d55b', 'https://ndp-chat-room.oss-cn-shenzhen.aliyuncs.com/releases/ForumFrontEnd-1.0.0.msi', CURRENT_TIMESTAMP),
    ('Windows', '0.9.0', '', '', 'c5e83b14d13e69546f6b5ec2369208a0061de152dc537b5c849723616f5f761b', 'https://ndp-chat-room.oss-cn-shenzhen.aliyuncs.com/releases/ForumFrontEnd-0.9.0.msi', CURRENT_TIMESTAMP),
    ('Windows', '0.8.0', '', '', '3bd4724329f7aa03c8ba34bf18e6fc0507e085380b09751b63bfaf37b8d87f45', 'https://ndp-chat-room.oss-cn-shenzhen.aliyuncs.com/releases/ForumFrontEnd-0.8.0.msi', CURRENT_TIMESTAMP);
