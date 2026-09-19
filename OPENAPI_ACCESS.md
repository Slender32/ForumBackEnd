# 前端获取 OpenAPI 文档

## 访问约定

API 基地址：本地 `http://localhost:8080`；当前服务器 `http://8.163.82.216/api`。
2026-09-19 本次发布已启用文档权限限制；此前线上旧版文档可匿名读取。

| 方法 | 相对 API 基地址的路径 | 用途 |
| --- | --- | --- |
| POST | `/auth/login` | 邮箱密码登录，返回双 token |
| GET | `/openapi/api-docs` | 完整 OpenAPI JSON |
| GET | `/openapi/api-docs/forum-backend` | forum-backend 分组 JSON |
| GET | `/openapi/api-docs.yaml` | 完整 OpenAPI YAML |
| GET | `/openapi/api-docs.yaml/forum-backend` | 分组 YAML |
| GET | `/openapi/api-docs/swagger-config` | Swagger UI 配置，也受权限保护 |
| GET | `/docs/openapi.html` | 文档工作台，使用账号（邮箱）与密码登录后查看及下载 JSON/YAML |

文档接口直接返回标准 OpenAPI 内容，不包裹业务 `Response`。页面本身公开，不内嵌文档；页面通过 /auth/login 登录后，在获取文档时提交访问令牌。令牌不放入 URL，不写入 localStorage 或 Cookie。

所有文档接口要求 `Authorization: Bearer <accessToken>`，账号必须具有 `docs:read` 权限。V10 迁移默认仅授予 ADMIN 角色；普通登录用户无权获取。以后可通过角色权限关系为开发者角色授予该权限，无需授予完整管理员权限。

角色权限保存在登录缓存中，因此已有管理员在迁移后需要重新登录才能获得新权限，单纯刷新 token 不保证重新加载权限。

## 前端调用示例

```javascript
// accessToken 来自现有登录流程的 data.accessToken。
async function fetchOpenApi(accessToken) {
  const response = await fetch('/api/openapi/api-docs', {
    headers: { Authorization: `Bearer ${accessToken}` },
    cache: 'no-store',
  });
  if (!response.ok) throw new Error(`获取文档失败：HTTP ${response.status}`);
  return response.json();
}
```

无 token、过期或登录缓存失效通常返回 401；有效 token 缺少权限返回 403；签名无效等错误沿用项目现有 400 响应。401 时复用现有刷新/重新登录流程，403 时应提示权限不足，避免循环刷新。

开发工具需支持在导入 URL 时附加 Authorization 请求头；不支持时可在文档页面下载后导入。原生 Swagger UI 的 Authorize 按钮不解决首次加载受保护文档的问题，请使用上述页面或前端自建带认证头的文档查看器。

## 部署

按现有部署流程发布后端，并应用 V10 迁移。Compose 增加 `FORUM_OPENAPI_SERVER_URL=/api`，使生成文档中的 servers 地址与 Nginx 代理前缀一致；本地默认 `/`。发布时需同步 Compose 配置并重建 app 容器。

当前部署文档中的公网入口为 HTTP。公网传输登录密码或 accessToken 前应配置 HTTPS；本次没有修改线上 TLS。

验收：匿名访问每个文档入口均为 401，普通用户为 403，有 docs:read 权限的用户为 200，返回文档 servers 地址为 `/api`。页面地址为 `/api/docs/openapi.html`，页面使用现有邮箱密码登录流程，仍由 docs:read 控制访问。退出文档只清除本页会话。页面内相对请求会保留 `/api` 前缀。
