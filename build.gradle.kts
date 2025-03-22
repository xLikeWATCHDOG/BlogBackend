buildscript {
    repositories {
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("maven-publish")
    id("org.springframework.boot.experimental.thin-launcher") version "1.0.31.RELEASE"
}

group = "com.birdy"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}

extra["springShellVersion"] = "3.4.0"

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.shell:spring-shell-starter")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.4.3")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.mybatis-flex:mybatis-flex-spring-boot-starter:1.10.8")
    implementation("org.mybatis:mybatis-spring:3.0.4")

    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("org.aspectj:aspectjweaver:1.9.22.1")
    implementation("org.aspectj:aspectjtools:1.9.22.1")
    implementation("cn.hutool:hutool-all:5.8.36")
    implementation("com.google.code.gson:gson:2.12.1")

    runtimeOnly("org.postgresql:postgresql")

    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("me.zhyd.oauth:JustAuth:1.16.7")
    implementation("com.alibaba:easyexcel:4.0.3")
    implementation("org.lionsoul:ip2region:2.7.0")
    implementation("com.google.guava:guava:33.4.0-jre")
    implementation("io.github.karlatemp:unsafe-accessor:1.7.0")
    implementation("com.tencentcloudapi:tencentcloud-sdk-java:3.1.1220")
    implementation("net.coobird:thumbnailator:0.4.20")

    implementation("com.aliyun:aliyun-java-sdk-core:4.7.3")
    implementation("com.aliyun:aliyun-java-sdk-dysmsapi:2.2.1")

    implementation("org.eclipse.angus:angus-mail:2.0.3")

    implementation("org.jetbrains:annotations:26.0.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.shell:spring-shell-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.shell:spring-shell-dependencies:${property("springShellVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.register<Delete>("cleanTmpJars") {
    delete(fileTree(buildDir) {
        include("**/tmp*.jar")
        include("**/resources/thinPom/")
    })
}

tasks.named("build") {
    finalizedBy("cleanTmpJars")
}
