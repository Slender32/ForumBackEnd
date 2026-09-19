INSERT INTO permissions (code, name, resource, action, description)
VALUES ('docs:read', 'Read API documentation', 'docs', 'read', 'Read generated OpenAPI specifications');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r CROSS JOIN permissions p
WHERE r.code = 'ADMIN' AND p.code = 'docs:read';
