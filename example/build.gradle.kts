group = "com.example"
version = "0.0.1-SNAPSHOT"

plugins {
    id("java")
    id("com.gradleup.shadow").version("8.3.6")
    id("io.spring.dependency-management").version("1.1.7")
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
}

dependencies {
    implementation("dev.ckateptb:WebMorph:0.0.2-20250619.002945-1")
    implementation(
        "org.bytedeco:ffmpeg:7.1-1.5.11:${
            System.getProperty("os.name").lowercase().split(" ")[0]
        }-x86_64"
    )

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes(
                "Main-Class" to "com.example.project.Application" // << Main class here
            )
        }
        mergeServiceFiles()
    }
    build {
        dependsOn(shadowJar)
    }
    jar {
        enabled = false
    }
}
