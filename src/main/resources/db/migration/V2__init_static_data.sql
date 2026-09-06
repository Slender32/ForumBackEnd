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
    ('comment:create', 'Create comment', 'comment', 'create', 'Create comments'),
    ('comment:delete:own', 'Delete own comment', 'comment', 'delete_own', 'Delete own comments'),
    ('comment:delete:any', 'Delete any comment', 'comment', 'delete_any', 'Delete any comment'),
    ('user:ban', 'Ban user', 'user', 'ban', 'Ban or unban users'),
    ('user:assign-role', 'Assign role', 'user', 'assign_role', 'Assign roles to users'),
    ('system:manage', 'Manage system', 'system', 'manage', 'Manage system settings');

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
    'comment:delete:any',
    'user:ban'
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
