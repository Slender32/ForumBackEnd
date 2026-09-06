package com.slender.forumbackend.configuration

import com.slender.forumbackend.constant.core.Http.LOGIN
import com.slender.forumbackend.model.data.LoginData
import com.slender.forumbackend.model.data.RefreshData
import com.slender.forumbackend.model.data.Response
import com.slender.forumbackend.model.data.UserData
import com.slender.forumbackend.model.request.LoginRequest
import io.swagger.v3.core.converter.ModelConverters.getInstance
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.customizers.GlobalOpenApiCustomizer
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.models.GroupedOpenApi.builder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Configuration
class OpenApi {
    private companion object {
        const val API_TITLE = "论坛后端"
        const val API_DESCRIPTION = "后端 REST API文档"
        const val API_VERSION = "0.0.1"
        const val CONTACT_NAME = "后端"
        const val SERVER_URL = "/"
        const val SERVER_DESCRIPTION = "当前服务器"
        const val SECURITY_SCHEME = "bearerAuth"
        const val SECURITY_SCHEME_NAME = "bearer"
        const val SECURITY_BEARER_FORMAT = "JWT"
        const val API_GROUP = "forum-backend"
        const val ALL_PATHS = "/**"
        const val ERROR_PATH = "/error"
        const val AUTH_TAG = "认证"
        const val LOGOUT_DOC_PATH = "/users/{uid}/logout"
        const val CODE_FIELD = "code"
        const val MESSAGE_FIELD = "message"
        const val TIMESTAMP_FIELD = "timestamp"
        const val DATA_FIELD = "data"
    }

    @Bean
    fun forumOpenApi(): OpenAPI = OpenAPI().apply {
        info = Info().apply {
            title = API_TITLE
            description = API_DESCRIPTION
            version = API_VERSION
            contact = Contact().apply {
                name = CONTACT_NAME
            }
        }
        servers = listOf(
            Server().apply {
                url = SERVER_URL
                description = SERVER_DESCRIPTION
            }
        )
        components = Components().apply {
            addSecuritySchemes(
                SECURITY_SCHEME,
                SecurityScheme().apply {
                    type = HTTP
                    scheme = SECURITY_SCHEME_NAME
                    bearerFormat = SECURITY_BEARER_FORMAT
                }
            )
        }
        addSecurityItem(
            SecurityRequirement().apply {
                addList(SECURITY_SCHEME)
            }
        )
    }

    @Bean
    fun forumApiGroup(): GroupedOpenApi = builder()
        .apply {
            group(API_GROUP)
            pathsToMatch(ALL_PATHS)
            pathsToExclude(ERROR_PATH)
            addOpenApiCustomizer { it.customizeAuth() }
        }.build()

    @Bean
    fun globalAuthOpenApiCustomizer() = GlobalOpenApiCustomizer { it.customizeAuth() }


    private final fun OpenAPI.customizeAuth() {
        registerAuthSchemas()
        path(
            LOGIN,
            PathItem().apply {
                post = loginOperation()
            }
        )
        path(
            LOGOUT_DOC_PATH,
            PathItem().apply {
                post = logoutOperation()
            }
        )
    }

    private final fun OpenAPI.registerAuthSchemas() {
        components = components ?: Components()
        listOf(
            LoginRequest::class.java,
            LoginData::class.java,
            RefreshData::class.java,
            UserData::class.java,
            Response::class.java,
        ).forEach {
            getInstance().read(it).forEach { (name, schema) ->
                components.addSchemas(name, schema)
            }
        }
    }

    private final fun loginOperation() = Operation().apply {
        tags = listOf(AUTH_TAG)
        operationId = "login"
        summary = "登录"
        description = "使用邮箱和密码登录。登录成功后返回accessToken、refreshToken、各自的过期时间戳和当前用户信息。" +
            "单设备登录策略：同一账号新登录会直接覆盖服务端旧的登录缓存。"
        security = emptyList()
        requestBody = RequestBody().apply {
            required = true
            description = "登录请求参数"
            content = jsonContent(schemaRef(LoginRequest::class.java.simpleName))
        }
        responses = ApiResponses().apply {
            addApiResponse("200", jsonResponse("登录成功", responseSchema(schemaRef("LoginData"))))
            addApiResponse("400", jsonResponse("1007 邮箱或密码错误；1008 请求参数错误", unitResponseSchema()))
            addApiResponse("403", jsonResponse("1005 用户被禁用", unitResponseSchema()))
            addApiResponse("500", jsonResponse("1500 服务器内部错误", unitResponseSchema()))
        }
    }

    private final fun logoutOperation() = Operation().apply {
        tags = listOf(AUTH_TAG)
        operationId = "logout"
        summary = "登出"
        description = "退出当前用户登录状态。请求头需携带 Authorization: Bearer <accessToken>。"
        addSecurityItem(SecurityRequirement().addList(SECURITY_SCHEME))
        addParametersItem(
            Parameter().apply {
                name = "uid"
                `in` = "path"
                required = true
                description = "用户ID"
                schema = Schema<Long>().apply {
                    type = "integer"
                    format = "int64"
                    example = 1
                }
            }
        )
        responses = ApiResponses().apply {
            addApiResponse("200", jsonResponse("登出成功", unitResponseSchema()))
            addApiResponse("400", jsonResponse("1006 令牌签名或格式错误", unitResponseSchema()))
            addApiResponse("401", jsonResponse("1001 令牌缺失；1002 accessToken已过期或登录状态失效", unitResponseSchema()))
            addApiResponse("403", jsonResponse("1005 用户被禁用；1009 权限不足", unitResponseSchema()))
        }
    }

    private final fun jsonResponse(
        description: String,
        schema: Schema<*>
    ) = ApiResponse().apply {
        this.description = description
        content = jsonContent(schema)
    }

    private final fun jsonContent(
        schema: Schema<*>
    ) = Content().apply {
        addMediaType(
            APPLICATION_JSON_VALUE,
            MediaType().apply {
                this.schema = schema
            }
        )
    }

    private final fun responseSchema(
        dataSchema: Schema<*>
    ) = ObjectSchema().apply {
        description = "统一接口响应"
        required = listOf(CODE_FIELD, TIMESTAMP_FIELD)
        addProperty(
            CODE_FIELD,
            Schema<Int>().apply {
                description = "业务状态码，0表示成功"
                type = "integer"
                format = "int32"
                example = 0
            }
        )
        addProperty(
            MESSAGE_FIELD,
            Schema<String>().apply {
                description = "响应消息"
                type = "string"
                nullable = true
                example = "操作成功"
            }
        )
        addProperty(
            TIMESTAMP_FIELD,
            Schema<Long>().apply {
                description = "响应时间戳，单位毫秒"
                type = "integer"
                format = "int64"
                example = 1767225600000
            }
        )
        addProperty(DATA_FIELD, dataSchema)
    }

    private final fun unitResponseSchema() = responseSchema(
        Schema<Any>().apply {
            description = "无响应数据"
            nullable = true
        }
    )

    private final fun schemaRef(schemaName: String) = Schema<Any>().apply {
        `$ref` = "#/components/schemas/$schemaName"
    }
}
