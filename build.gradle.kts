import org.gradle.jvm.toolchain.JavaLanguageVersion.of

plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.slender"
version = "0.0.1-SNAPSHOT"
description = "ForumBackEnd"

val mockitoAgent = configurations.create("mockitoAgent") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    runtime()
    test()
    other()
}

fun DependencyHandlerScope.runtime(){
    //starter
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop:4.0.0-M2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

    //my dependencies
    implementation("io.jsonwebtoken:jjwt:0.13.0")
    implementation("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17")
    implementation("com.baomidou:mybatis-plus-jsqlparser:3.5.17")
    implementation("com.aliyun:alibabacloud-oss-v2:0.5.1")
    implementation("org.sejda.imageio:webp-imageio:0.1.6")
    implementation("com.bucket4j:bucket4j_jdk17-core:8.14.0")
    implementation("com.github.ben-manes.caffeine:caffeine")

    //relational dependencies
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("tools.jackson.module:jackson-module-kotlin")
}

fun DependencyHandlerScope.test(){
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    add(mockitoAgent.name, "org.mockito:mockito-core") {
        isTransitive = false
    }
    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

fun DependencyHandlerScope.other(){
    runtimeOnly("org.postgresql:postgresql")

    //Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

java {
    toolchain {
        languageVersion = of(21)
    }
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}", "-Xshare:off")
}

tasks.register<JavaExec>("fullStackRun") {
    description = "Starts the full React -> Nginx -> Spring Boot stack and keeps it running."
    group = "application"
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.slender.forumbackend.fullstack.FullStackApplicationKt")
    dependsOn(tasks.named("bootJar"), tasks.named("testClasses"))
    standardInput = System.`in`
    System.getenv()
        .filterKeys { it.equals("PATH", ignoreCase = true) }
        .forEach { (key, value) ->
            environment(
                key,
                value.filterNot { it in "\u202A\u202B\u202C\u202D\u202E\u2066\u2067\u2068\u2069" },
            )
        }
}
