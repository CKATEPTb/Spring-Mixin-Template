plugins {
    java
    id("com.gradleup.shadow").version("8.3.6")
    id("io.spring.dependency-management").version("1.1.7")
}

group = "dev.ckateptb"
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
}

dependencies {
    // Mixin
    implementation("net.lenni0451.classtransform:core:1.14.1")
    annotationProcessor("net.lenni0451.classtransform:mixinsdummy:1.14.1")
    implementation("net.lenni0451.classtransform:mixinsdummy:1.14.1")
    implementation("net.lenni0451.classtransform:mixinstranslator:1.14.1")
    implementation("net.lenni0451.classtransform:additionalclassprovider:1.14.1")
    // Reflection
    implementation("net.lenni0451:Reflect:1.5.0")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    implementation("org.springframework.boot:spring-boot-starter:3.5.0")
}

var application = "${project.group}.${project.name.lowercase()}.Application"
tasks {
    shadowJar {
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
    jar {
        manifest {
            attributes(
                "Main-Class" to application,
                "Agent-Class" to application,
                "Premain-Class" to application,
            )
        }
    }
}