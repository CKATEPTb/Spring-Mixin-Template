import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

group = "dev.ckateptb"
version = "0.1.0-SNAPSHOT"

plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow").version("8.3.6")
    id("io.spring.dependency-management").version("1.1.7")
    id("io.github.gradle-nexus.publish-plugin").version("1.1.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    all {
        exclude(module = "spring-boot-starter-logging")
        exclude(group = "ch.qos.logback")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.jyraf.com/repository/maven-snapshots/")
    maven("https://repo.jyraf.com/repository/maven-releases/")
}

dependencies {
    // Video
    implementation("org.bytedeco:javacv:1.5.11")
    compileOnly(
        "org.bytedeco:ffmpeg:7.1-1.5.11:${
            System.getProperty("os.name").lowercase().split(" ")[0]
        }-x86_64"
    )

    // Mixin
    implementation("net.lenni0451.classtransform:core:1.14.1")
    annotationProcessor("net.lenni0451.classtransform:mixinsdummy:1.14.1")
    implementation("net.lenni0451.classtransform:mixinsdummy:1.14.1")
    implementation("net.lenni0451.classtransform:mixinstranslator:1.14.1")
    implementation("net.lenni0451.classtransform:additionalclassprovider:1.14.1")
    implementation("net.lenni0451:Reflect:1.5.0")

    // Reflection
    implementation("dev.ckateptb:Reflect:2.0.0-SNAPSHOT")

    // Logs
    implementation("org.apache.logging.log4j:log4j-api:2.24.3")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.3")

    // Permissions
    implementation("net.luckperms:standalone:5.5.5")

    // ObjectMapper
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // JWT
    implementation("com.auth0:java-jwt:4.2.1")

    // Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-rsocket:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-security:3.5.0")
    implementation("org.springframework.security:spring-security-messaging:6.5.0")
    implementation("org.springframework.security:spring-security-rsocket:6.5.0")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
        transform(Log4j2PluginsCacheFileTransformer::class.java)
    }
    build {
        dependsOn(shadowJar)
    }
    jar {
        enabled = false
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.getByName("shadowJar").outputs.files.singleFile)
        }
    }
}

nexusPublishing {
    repositories {
        create("jyrafRepo") {
            nexusUrl.set(uri("https://repo.jyraf.com/"))
            snapshotRepositoryUrl.set(uri("https://repo.jyraf.com/repository/maven-snapshots/"))
            username.set(System.getenv("NEXUS_USERNAME"))
            password.set(System.getenv("NEXUS_PASSWORD"))
        }
    }
}